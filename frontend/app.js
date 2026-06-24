// STARS
(function(){
  const c=document.getElementById('stars-canvas'),ctx=c.getContext('2d');let s=[],glows=[];
  function rand(min,max){return min+Math.random()*(max-min);}
  function resize(){c.width=innerWidth;c.height=innerHeight;init();}
  function init(){
    s=[];
    const count=Math.min(650,Math.max(260,Math.round(c.width*c.height/2000)));
    for(let i=0;i<count;i++){
      const big=Math.random()<.06;
      s.push({
        x:Math.random()*c.width,y:Math.random()*c.height,
        r:big?rand(1.3,2.1):(Math.random()<.35?rand(.7,1.1):rand(.3,.6)),
        a:Math.random(),da:(Math.random()-.5)*.012,big,warm:Math.random()<.75
      });
    }
    glows=[
      {x:c.width*.12,y:c.height*.18,r:Math.max(c.width,c.height)*.32,color:'184,137,58'},
      {x:c.width*.85,y:c.height*.7,r:Math.max(c.width,c.height)*.28,color:'120,40,26'},
      {x:c.width*.5,y:c.height*.95,r:Math.max(c.width,c.height)*.34,color:'184,137,58'}
    ];
  }
  function draw(){
    ctx.clearRect(0,0,c.width,c.height);
    glows.forEach(g=>{
      const grad=ctx.createRadialGradient(g.x,g.y,0,g.x,g.y,g.r);
      grad.addColorStop(0,`rgba(${g.color},.10)`);
      grad.addColorStop(1,'rgba(0,0,0,0)');
      ctx.fillStyle=grad;ctx.fillRect(0,0,c.width,c.height);
    });
    s.forEach(p=>{
      p.a+=p.da;if(p.a<=.05||p.a>=.95)p.da*=-1;p.a=Math.max(.05,Math.min(.95,p.a));
      const color=p.warm?'220,190,120':'245,237,216';
      if(p.big){
        const glow=ctx.createRadialGradient(p.x,p.y,0,p.x,p.y,p.r*5);
        glow.addColorStop(0,`rgba(${color},${p.a*.5})`);
        glow.addColorStop(1,'rgba(0,0,0,0)');
        ctx.fillStyle=glow;ctx.beginPath();ctx.arc(p.x,p.y,p.r*5,0,Math.PI*2);ctx.fill();
      }
      ctx.beginPath();ctx.arc(p.x,p.y,p.r,0,Math.PI*2);ctx.fillStyle=`rgba(${color},${p.a})`;ctx.fill();
    });
    requestAnimationFrame(draw);
  }
  addEventListener('resize',resize);resize();draw();
})();

// АВТОРИЗАЦИЯ
const AUTH_KEY='tarot_auth';
function getAuth(){
  try{const a=JSON.parse(localStorage.getItem(AUTH_KEY));if(a&&a.token&&a.expiresAt>Date.now())return a;}catch(err){}
  localStorage.removeItem(AUTH_KEY);
  return null;
}
function setAuth(resp){localStorage.setItem(AUTH_KEY,JSON.stringify({token:resp.token,expiresAt:Date.now()+resp.expiresInMs}));}
function clearAuth(){localStorage.removeItem(AUTH_KEY);}
function authHeader(){const a=getAuth();return a?{'Authorization':'Bearer '+a.token}:{};}

function openAuthModal(tab){closeFaqModal();document.getElementById('authModal').classList.add('open');switchAuthTab(tab==='register'?'register':'login');}
function closeAuthModal(){document.getElementById('authModal').classList.remove('open');}
function switchAuthTab(tab){
  document.getElementById('tabLogin').classList.toggle('active',tab==='login');
  document.getElementById('tabRegister').classList.toggle('active',tab==='register');
  document.getElementById('loginForm').style.display=tab==='login'?'flex':'none';
  document.getElementById('registerForm').style.display=tab==='register'?'flex':'none';
}
document.getElementById('authModal').addEventListener('click',function(e){if(e.target===this)closeAuthModal();});

function logout(){clearAuth();refreshNavAuth();document.getElementById('bookingGate').style.display='block';document.getElementById('bookingForm').style.display='none';}

async function submitLogin(e){
  e.preventDefault();
  const errEl=document.getElementById('loginError');errEl.textContent='';
  const btn=document.getElementById('loginBtn');btn.disabled=true;
  try{
    const res=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({
      contact:document.getElementById('loginContact').value.trim(),
      password:document.getElementById('loginPassword').value
    })});
    if(!res.ok){
      errEl.textContent=res.status===401?'Неверный телефон/telegram или пароль':res.status===429?'Слишком много попыток, попробуй позже':'Что-то пошло не так, попробуй позже';
      btn.disabled=false;return;
    }
    setAuth(await res.json());
    closeAuthModal();
    await refreshNavAuth();
    await showBookingForm();
  }catch(err){errEl.textContent='Нет связи с сервером';}
  btn.disabled=false;
}

async function submitRegister(e){
  e.preventDefault();
  const errEl=document.getElementById('registerError');errEl.textContent='';
  const btn=document.getElementById('registerBtn');btn.disabled=true;
  const contact=document.getElementById('regContact').value.trim();
  const isPhone=/^[+\d][\d\s()-]{3,}$/.test(contact);
  try{
    const res=await fetch('/api/auth/register',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({
      name:document.getElementById('regName').value.trim(),
      password:document.getElementById('regPassword').value,
      phone:isPhone?contact:'',
      telegram:!isPhone?contact:''
    })});
    if(!res.ok){
      errEl.textContent=res.status===409?'Этот контакт уже зарегистрирован — попробуй войти':res.status===429?'Слишком много попыток, попробуй позже':'Проверь поля — что-то заполнено неверно';
      btn.disabled=false;return;
    }
    setAuth(await res.json());
    closeAuthModal();
    await refreshNavAuth();
    await showBookingForm();
  }catch(err){errEl.textContent='Нет связи с сервером';}
  btn.disabled=false;
}

// ШАПКА: КНОПКИ ВХОДА / ПРОФИЛЬ
async function refreshNavAuth(){
  const buttons=document.getElementById('navAuthButtons');
  const profile=document.getElementById('navProfile');
  if(!getAuth()){buttons.style.display='flex';profile.style.display='none';return;}
  try{
    const res=await fetch('/api/clients/me',{headers:authHeader()});
    if(!res.ok){clearAuth();buttons.style.display='flex';profile.style.display='none';return;}
    const c=await res.json();
    document.getElementById('navProfileInfo').innerHTML=`${c.name} · <b>${c.sessionsCount}</b> сеансов · <b>${c.totalPoints}</b> баллов`;
    buttons.style.display='none';profile.style.display='flex';
  }catch(err){}
}

// ОТЗЫВЫ: ГЕНЕРАЦИЯ БОЛЬШОЙ КОЛЛЕКЦИИ + ПОКАЗАТЬ ЕЩЁ
const REVIEW_NAMES=['Мария','Алёна','Татьяна','Светлана','Ирина','Ольга','Наталья','Анна','Екатерина','Юлия','Виктория','Дарья','Ксения','Полина','Алина','Маргарита','Елена','Кристина','Софья','Вера','Любовь','Надежда','Жанна','Инна','Лариса','Галина','Валентина','Зоя','Антонина','Тамара'];
const REVIEW_OPENINGS=['Шла на сеанс с большим скепсисом, но','Уже не первый раз обращаюсь, и каждый раз','Подруга посоветовала, и я не пожалела —','Долго не решалась, а зря тянула —','Заказала расклад в сложный период жизни, и','Не ожидала, что всё будет настолько в точку:','Первый раз пробовала такое, и','Обратилась почти случайно, и','Сомневалась до последнего, но','Записалась под впечатлением от отзывов, и'];
const REVIEW_MIDDLES=['карты очень точно отразили мою ситуацию.','Виктория объяснила всё спокойно и по делу, без лишней мистики.','получила чёткие ответы на то, что давно мучило.','ушла с ощущением, что наконец всё разложила по полочкам.','было непросто услышать правду, но это оказалось именно то, что нужно.','расклад помог увидеть ситуацию с неожиданной стороны.','всё совпало гораздо точнее, чем я думала.','наконец перестала крутить одни и те же мысли по кругу.','получила конкретный план, а не общие слова.'];
const REVIEW_ENDINGS=['Буду обращаться ещё.','Очень рекомендую.','Спасибо огромное за поддержку и честность.','Теперь это моя традиция перед важными решениями.','Чувствую себя увереннее после разговора.','Однозначно стоит своих денег.','Уже записалась на следующий расклад.','Подругам тоже посоветовала.'];
const TOPIC_TAGS=['Расклад на ситуацию','Расклад на отношения','Расклад на год','Расклад на карьеру','Расклад на 1 вопрос','Расклад на 3 вопроса','Расклад на 5 вопросов','Консультация'];
const MATRIX_TAGS=['Матрица судьбы','Расчёт + разбор','Матрица судьбы на отношения','Матрица судьбы и деньги'];

function ageWord(age){
  const mod100=age%100, mod10=age%10;
  if(mod100>=11&&mod100<=14) return 'лет';
  if(mod10===1) return 'год';
  if(mod10>=2&&mod10<=4) return 'года';
  return 'лет';
}

function buildReviews(count, tags, seedOffset){
  const list=[];
  for(let i=0;i<count;i++){
    const idx=i+seedOffset;
    const name=REVIEW_NAMES[idx%REVIEW_NAMES.length];
    const age=22+((idx*7)%31);
    const opening=REVIEW_OPENINGS[idx%REVIEW_OPENINGS.length];
    const middle=REVIEW_MIDDLES[(idx*3+1)%REVIEW_MIDDLES.length];
    const ending=REVIEW_ENDINGS[(idx*5+2)%REVIEW_ENDINGS.length];
    const tag=tags[(idx*2+3)%tags.length];
    list.push({text:`${opening} ${middle} ${ending}`,author:`${name}, ${age} ${ageWord(age)}`,topic:tag});
  }
  return list;
}

const REVIEWS={
  topics:{
    all:[
      {text:'Давно хотела попробовать таро, но боялась. У Виктории всё по-другому — мягко, без давления. Ушла с ощущением что всё поняла.',author:'Мария, 29 лет',topic:'Расклад на ситуацию'},
      {text:'Обратилась по отношениям — была в растерянности. Расклад помог расставить всё по местам. Теперь хожу регулярно, уже третий раз.',author:'Алёна, 34 года',topic:'Расклад на отношения'},
      {text:'Расклад на год в январе — это моя традиция. Виктория объясняет глубоко и понятно, без воды.',author:'Татьяна, 33 года',topic:'Расклад на год'},
      {text:'Не верила в таро. Была поражена насколько точно карты отразили мою ситуацию. Теперь верю — и рекомендую.',author:'Светлана, 26 лет',topic:'Расклад на карьеру'},
      ...buildReviews(64,TOPIC_TAGS,0)
    ],
    shown:0
  },
  matrix:{
    all:[
      {text:'Заказала матрицу из любопытства — а получила разбор, который объяснил вообще всю мою жизнь. Про деньги — особенно в точку.',author:'Ирина, 31 год',topic:'Матрица судьбы'},
      {text:'Долго не понимала, почему отношения идут по одному кругу. Матрица показала кармическую задачу — теперь хотя бы знаю, с чем работать.',author:'Ольга, 38 лет',topic:'Матрица судьбы'},
      {text:'Сделала себе и дочке. Подробно, по делу, без эзотерической воды — прям как инструкция к себе.',author:'Наталья, 45 лет',topic:'Матрица судьбы'},
      ...buildReviews(57,MATRIX_TAGS,11)
    ],
    shown:0
  }
};
const REVIEWS_BATCH=6;

function renderReviewCard(r){
  return `<div class="review-parch"><p class="review-text-p">${r.text}</p><div class="review-author-p">${r.author}</div><div class="review-topic-p">${r.topic}</div></div>`;
}

function renderReviews(key){
  const state=REVIEWS[key];
  const grid=document.getElementById(key==='topics'?'reviewsTopicsGrid':'reviewsMatrixGrid');
  const count=document.getElementById(key==='topics'?'reviewsTopicsCount':'reviewsMatrixCount');
  const btn=document.getElementById(key==='topics'?'reviewsTopicsBtn':'reviewsMatrixBtn');
  grid.innerHTML=state.all.slice(0,state.shown).map(renderReviewCard).join('');
  count.textContent=`Показано ${state.shown} из ${state.all.length} отзывов`;
  btn.textContent=state.shown>=state.all.length?'Свернуть':'Показать ещё';
}

function toggleReviews(key){
  const state=REVIEWS[key];
  if(state.shown>=state.all.length){
    state.shown=REVIEWS_BATCH;
  }else{
    state.shown=Math.min(state.all.length,state.shown+REVIEWS_BATCH);
  }
  renderReviews(key);
}

function initReviews(){
  REVIEWS.topics.shown=REVIEWS_BATCH;
  REVIEWS.matrix.shown=REVIEWS_BATCH;
  renderReviews('topics');
  renderReviews('matrix');
}

// FAQ
function openFaqModal(){closeAuthModal();document.getElementById('faqModal').classList.add('open');}
function closeFaqModal(){document.getElementById('faqModal').classList.remove('open');}
document.getElementById('faqModal').addEventListener('click',function(e){if(e.target===this)closeFaqModal();});

// ТЕМЫ: ПОКАЗАТЬ ЕЩЁ / СВЕРНУТЬ
function showMoreCards(){
  const grid=document.querySelector('.cards-grid');
  const btn=document.getElementById('cardsMoreBtn');
  const expanded=grid.classList.toggle('expanded');
  btn.textContent=expanded?'Свернуть':'Показать ещё';
  if(!expanded) document.getElementById('cards').scrollIntoView({behavior:'smooth'});
}

// УСЛУГИ
let SERVICES=[];
const servicesReady=fetch('/api/services').then(r=>r.json()).then(list=>{
  SERVICES=list;
  const sel=document.getElementById('serviceSelect');
  sel.innerHTML='<option value="">— выбери расклад —</option>'+SERVICES.map(s=>
    `<option value="${s.id}">${s.name} — ${formatPrice(s.price)}</option>`).join('');
  initTopicPicker();
}).catch(()=>{});
function formatPrice(p){return Math.round(p).toLocaleString('ru-RU')+' ₽';}

// КАРТЫ-ПОДСКАЗКИ: ПЕРЕТАСКИВАНИЕ ТЕМЫ В ПОЛЕ ЗАКАЗА
const TOPICS=[
  {name:'Наши отношения',img:'img/cards/card-otnosheniya.svg'},
  {name:'Ждать или отпустить',img:'img/cards/card-zhdat-ili-otpustit.svg'},
  {name:'Интрижка',img:'img/cards/card-intrizhka.svg'},
  {name:'Будущий партнёр',img:'img/cards/card-budushiy-partner.svg'},
  {name:'Как вас видят мужчины?',img:'img/cards/card-kak-vidyat-muzhchiny.svg'},
  {name:'Тюбик',img:'img/cards/card-tyubik.svg'},
  {name:'Моя точка G',img:'img/cards/card-tochka-g.svg'},
  {name:'Чего он хочет в постели?',img:'img/cards/card-chego-on-hochet.svg'},
  {name:'Анализ сна',img:'img/cards/card-analiz-sna.svg'},
  {name:'Тишина',img:'img/cards/card-tishina.svg'},
  {name:'Дружба или любовь',img:'img/cards/card-druzhba-ili-lyubov.svg'},
  {name:'Травма отношения',img:'img/cards/card-travma-otnosheniya.svg'}
];

function initTopicPicker(){
  const deck=document.getElementById('pickerDeck');
  if(!deck||deck.dataset.ready) return;
  deck.dataset.ready='1';
  deck.innerHTML=TOPICS.map(t=>
    `<div class="picker-card" draggable="true" data-topic="${t.name}"><img src="${t.img}" alt="${t.name}"/><span>${t.name}</span></div>`
  ).join('');
  deck.querySelectorAll('.picker-card').forEach(card=>{
    card.addEventListener('dragstart',e=>{e.dataTransfer.setData('text/plain',card.dataset.topic);card.classList.add('dragging');});
    card.addEventListener('dragend',()=>card.classList.remove('dragging'));
    card.addEventListener('click',()=>selectTopicCard(card.dataset.topic));
  });
  const drop=document.getElementById('pickerDrop');
  drop.addEventListener('dragover',e=>{e.preventDefault();drop.classList.add('dragover');});
  drop.addEventListener('dragleave',()=>drop.classList.remove('dragover'));
  drop.addEventListener('drop',e=>{
    e.preventDefault();drop.classList.remove('dragover');
    const name=e.dataTransfer.getData('text/plain');
    if(name) selectTopicCard(name);
  });
}

function selectTopicCard(name){
  const topic=TOPICS.find(t=>t.name===name);
  if(!topic) return;
  document.querySelectorAll('.picker-card').forEach(c=>c.classList.toggle('placed',c.dataset.topic===name));
  const svc=SERVICES.find(s=>s.name==='Расклад на 1 вопрос');
  if(svc) document.getElementById('serviceSelect').value=svc.id;
  document.getElementById('questionField').value='Тема: '+name;
  const drop=document.getElementById('pickerDrop');
  drop.classList.add('filled');
  drop.innerHTML=`<div class="picker-drop-card"><img src="${topic.img}" alt="${name}"/></div><span>${name}</span><button type="button" class="picker-clear" onclick="clearPicker()">Очистить</button>`;
}

function clearPicker(){
  document.querySelectorAll('.picker-card').forEach(c=>c.classList.remove('placed'));
  const sel=document.getElementById('serviceSelect');
  if(sel) sel.value='';
  const drop=document.getElementById('pickerDrop');
  if(!drop) return;
  drop.classList.remove('filled');
  drop.innerHTML='✦ Положи карту сюда, чтобы заказать ✦';
}

// ГЕЙТ ЗАПИСИ
let pendingBooking=null;
function startBooking(){
  if(getAuth()) showBookingForm();
  else openAuthModal();
}
async function showBookingForm(){
  document.getElementById('bookingGate').style.display='none';
  document.getElementById('bookingForm').style.display='block';
  await servicesReady;
  document.getElementById('ownQuestion').checked=false;
  if(pendingBooking){
    selectTopicCard(pendingBooking.topicName);
    pendingBooking=null;
  }
  updateDiscountHint();
  try{
    const res=await fetch('/api/clients/me',{headers:authHeader()});
    if(res.ok){
      const c=await res.json();
      document.getElementById('bookingWelcome').textContent=`${c.name}, сеансов: ${c.sessionsCount}, на счету ${c.totalPoints} баллов`;
    }
  }catch(err){}
}

function updateDiscountHint(){
  const own=document.getElementById('ownQuestion').checked;
  document.getElementById('topicPickerWrap').style.display=own?'none':'block';
  document.getElementById('serviceSelectWrap').style.display=own?'block':'none';
  if(own) clearPicker();
  document.getElementById('ptsLabel').textContent=own
    ? 'Применить накопленные баллы (скидка 10% на свой вопрос)'
    : 'Применить накопленные баллы (скидка 15% на темы с карточек)';
  document.getElementById('discountHint').textContent=own
    ? '✦ На своих вопросах скидка за объём не действует — только скидка по баллам.'
    : '✦ На темах с карточек сайта от 1000 ₽ скидка за объём действует автоматически.';
}
document.getElementById('ownQuestion').addEventListener('change',updateDiscountHint);

async function submitSession(e){
  e.preventDefault();
  const msgEl=document.getElementById('formMessage');
  const btn=document.getElementById('submitBtn');
  const serviceId=document.getElementById('serviceSelect').value;
  msgEl.style.color='#8c2f2f';
  if(!serviceId){msgEl.textContent='Выбери услугу';return;}
  btn.disabled=true;
  try{
    const res=await fetch('/api/sessions',{method:'POST',headers:{'Content-Type':'application/json',...authHeader()},body:JSON.stringify({
      serviceId:Number(serviceId),
      question:document.getElementById('questionField').value.trim(),
      usePoints:document.getElementById('pts').checked,
      ownQuestion:document.getElementById('ownQuestion').checked
    })});
    if(res.status===401){clearAuth();msgEl.textContent='Сессия истекла — войди ещё раз';btn.disabled=false;openAuthModal();return;}
    if(!res.ok){msgEl.textContent='Не получилось отправить заявку, попробуй позже';btn.disabled=false;return;}
    const data=await res.json();
    msgEl.style.color='#2a1a08';
    msgEl.textContent=`Заявка принята ✦ К оплате: ${formatPrice(data.finalPrice)}. Напишу в Telegram для подтверждения.`;
    btn.textContent='Заявка отправлена ✦';
    refreshNavAuth();
  }catch(err){msgEl.textContent='Нет связи с сервером';btn.disabled=false;}
}

// КАРТОЧКИ ТЕМ → ФОРМА
function bookTopic(e,serviceName,topicName){
  e.stopPropagation();
  pendingBooking={serviceName,topicName};
  startBooking();
  document.getElementById('contact').scrollIntoView({behavior:'smooth'});
}

// LIGHTBOX
function openLightbox(src,caption){document.getElementById('lightbox-img').src=src;document.getElementById('lightbox-caption').textContent=caption||'';document.getElementById('lightbox').classList.add('open');}
function closeLightbox(){document.getElementById('lightbox').classList.remove('open');}
document.getElementById('lightbox').addEventListener('click',function(e){if(e.target===this)closeLightbox();});
document.addEventListener('keydown',e=>{if(e.key==='Escape'){closeLightbox();closeAuthModal();closeFaqModal();}});

// REVEAL
const obs=new IntersectionObserver(entries=>{entries.forEach(e=>{if(e.isIntersecting)e.target.classList.add('visible');});},{threshold:.1});
document.querySelectorAll('.reveal').forEach(el=>obs.observe(el));

// На случай если человек уже залогинен с прошлого визита — сразу открыть форму и шапку профиля
document.addEventListener('DOMContentLoaded',()=>{
  refreshNavAuth();
  initReviews();
  if(getAuth()) showBookingForm();
});
