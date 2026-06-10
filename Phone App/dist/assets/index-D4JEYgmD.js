(function(){const t=document.createElement("link").relList;if(t&&t.supports&&t.supports("modulepreload"))return;for(const r of document.querySelectorAll('link[rel="modulepreload"]'))n(r);new MutationObserver(r=>{for(const s of r)if(s.type==="childList")for(const p of s.addedNodes)p.tagName==="LINK"&&p.rel==="modulepreload"&&n(p)}).observe(document,{childList:!0,subtree:!0});function a(r){const s={};return r.integrity&&(s.integrity=r.integrity),r.referrerPolicy&&(s.referrerPolicy=r.referrerPolicy),r.crossOrigin==="use-credentials"?s.credentials="include":r.crossOrigin==="anonymous"?s.credentials="omit":s.credentials="same-origin",s}function n(r){if(r.ep)return;r.ep=!0;const s=a(r);fetch(r.href,s)}})();const G=e=>`astory-state-${e.toLowerCase()}`,P={onboardingComplete:!1,activeStoryId:null,stories:[]};function Ee(e){try{const t=localStorage.getItem(G(e));return t?{...structuredClone(P),...JSON.parse(t)}:structuredClone(P)}catch{return structuredClone(P)}}function Pe(e,t){localStorage.setItem(G(e),JSON.stringify(t))}function Fe(e){return localStorage.removeItem(G(e)),structuredClone(P)}function We({name:e,lastName:t,relationship:a,relationshipOther:n,birthMonth:r,birthYear:s,hasAccount:p,connectedEmail:w}){return{id:crypto.randomUUID(),name:e||"",lastName:t||"",relationship:a||"Relative",relationshipOther:n||"",birthMonth:r||"",birthYear:s||"",hasAccount:p||null,connectedEmail:w||"",photo:null,intro:"",memories:[],sessionsCompleted:0,doneTopicsToday:{date:"",eras:[]},createdAt:new Date().toISOString()}}function Re({title:e,body:t,era:a,excerpt:n,rating:r,ratingNote:s,period:p,privacy:w}){return{id:crypto.randomUUID(),title:e,body:t,era:a||"Life",excerpt:n||t.slice(0,140),rating:r||0,ratingNote:s||"",period:p||"",privacy:w,photos:[],createdAt:new Date().toISOString()}}const N=["Childhood","School years","Early adulthood","Career","Love & family","Later years","Wisdom"],je=["Grandmother","Grandfather","Mother","Father","Spouse","Sibling","Relative","Friend","Myself","Other"],Ie={Grandmother:"#c9915a",Grandfather:"#c9915a",Mother:"#6aac88",Father:"#6aac88",Spouse:"#b3709e",Sibling:"#6d8dc2",Relative:"#b89060",Friend:"#8d78c4",Myself:"#d4a574",Other:"#a09070"},Ye=[{first:"Tell me about your childhood home.",third:"Tell me about {name}'s childhood home.",followUp:"What sounds or smells come back to you when you think of it?",era:"Childhood"},{first:"Who was the first person who truly believed in you?",third:"Who was the first person who truly believed in {name}?",followUp:"What did they do or say that still stays with you?",era:"Childhood"},{first:"Describe a day you felt completely alive.",third:"Describe a day {name} has talked about feeling most alive.",followUp:"Where were you, and who else was there?",era:"Early adulthood"},{first:"What was falling in love like the first time?",third:"What do you know about {name}'s first experience of falling in love?",followUp:"What do you remember most about that feeling?",era:"Love & family"},{first:"Tell me about work that genuinely mattered to you.",third:"Tell me about work that genuinely mattered to {name}.",followUp:"What made you proud of it, even on the hard days?",era:"Career"},{first:"What's a lesson you learned the hard way?",third:"What's a hard lesson {name} has shared with you?",followUp:"Looking back now — what would you tell someone going through the same thing?",era:"Wisdom"},{first:"Tell me about a place that shaped who you are.",third:"Tell me about a place that shaped who {name} is.",followUp:"What did being there teach you?",era:"Childhood"},{first:"What's something you did that surprised even yourself?",third:"What's something {name} did that surprised the people around them?",followUp:"Where did that courage or clarity come from?",era:"Early adulthood"}];function ze(e,t){return Ye.map(a=>({question:e?a.first:a.third.replace(/\{name\}/g,t||"them"),followUp:a.followUp,era:a.era}))}const he=["That's such a vivid picture.","I love how you describe that.","What a detail to hold onto.","You paint it so clearly.","There's something so alive in what you shared.","That stays with you, doesn't it.","I can almost picture it."],ve=["Take your time — even a single image or word is worth keeping.","Whatever comes to mind first is just fine. There's no right answer.","There's no rush here. Just whatever feels natural."];function Ge(e,t){return e.trim().length<25?`${ve[Math.floor(Math.random()*ve.length)]}

${t}`:`${he[Math.floor(Math.random()*he.length)]}

${t}`}function Ve(e,t,a){const r=e.join(" ").trim().length,s=t?"your":`${a||"their"}'s`;return r<50?`Even these few words carry something real. ${t?"Your":`${a}'s`} family will be so glad this was written down.`:r<180?"There's genuine warmth in what you've shared. These are the kinds of details that stay with families — the ones that get passed down for generations.":`Thank you for trusting this with me. The texture of what you've described — the specific details, the feeling — that's exactly what ${s} family will treasure most.`}const Ue=["Childhood","School years","Early adulthood","Career","Love & family","Later years"];function _e(e,t,a){const n=e?"your":`${t}'s`;return Ue.includes(a)?`Do you remember roughly when this happened — the year, or what stage of ${n} life it was?`:`What period of ${n} life does this wisdom come from most?`}function Je(e,t){const a=t.filter(Boolean).join(`

`),n=e.question.replace(/\?$/,""),r=n.length>50?`${n.slice(0,47)}…`:n,s=a.trim()||"A memory waiting to be filled in.",p=s.length>160?`${s.slice(0,157)}…`:s;return{title:r,body:s,excerpt:p,era:e.era}}function B(e){return e.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;")}function be(e){return new Date(e).toLocaleDateString(void 0,{month:"long",day:"numeric",year:"numeric"})}function Ke(e){const t=e.profile.path==="preserve"&&e.profile.subjectName?e.profile.subjectName:e.profile.name||"A Story",a=N.map(p=>({era:p,items:e.memories.filter(w=>w.era===p)})).filter(p=>p.items.length),n=e.memories.filter(p=>!N.includes(p.era));n.length&&a.push({era:"Life",items:n});const r=a.length?a.map(p=>`
        <section class="print-chapter">
          <h2>${B(p.era)}</h2>
          ${p.items.map(w=>`
            <article class="print-memory">
              <h3>${B(w.title)}</h3>
              <p class="print-date">${be(w.createdAt)}</p>
              <div class="print-body">${B(w.body).replace(/\n/g,"<br>")}</div>
            </article>`).join("")}
        </section>`).join(""):'<p class="print-empty">No memories yet. Return to the app and share your story — then print again.</p>',s=window.open("","_blank","noopener,noreferrer");if(!s){alert("Please allow pop-ups to print your legacy, or try again.");return}s.document.write(`<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>${B(t)} — Legacy</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: Georgia, "Times New Roman", serif;
      font-size: 14pt;
      line-height: 1.65;
      color: #1c1b18;
      padding: 0.75in;
      max-width: 7in;
      margin: 0 auto;
    }
    .print-header {
      text-align: center;
      margin-bottom: 2rem;
      padding-bottom: 1.5rem;
      border-bottom: 2px solid #1f6b5c;
    }
    .print-header h1 {
      font-size: 28pt;
      font-weight: normal;
      color: #1f6b5c;
      margin-bottom: 0.35rem;
    }
    .print-header .subtitle {
      font-size: 12pt;
      color: #4a4843;
      font-style: italic;
    }
    .print-tagline {
      margin-top: 1rem;
      font-size: 11pt;
      color: #5c5650;
    }
    .print-chapter { margin-bottom: 2rem; page-break-inside: avoid; }
    .print-chapter h2 {
      font-size: 18pt;
      color: #1f6b5c;
      margin-bottom: 1rem;
      border-bottom: 1px solid #ccc;
      padding-bottom: 0.35rem;
    }
    .print-memory {
      margin-bottom: 1.5rem;
      page-break-inside: avoid;
    }
    .print-memory h3 { font-size: 14pt; margin-bottom: 0.25rem; }
    .print-date { font-size: 10pt; color: #5c5650; margin-bottom: 0.75rem; }
    .print-body { font-size: 12pt; }
    .print-footer {
      margin-top: 3rem;
      padding-top: 1rem;
      border-top: 1px solid #ccc;
      font-size: 10pt;
      color: #5c5650;
      text-align: center;
    }
    .print-empty { font-style: italic; color: #5c5650; }
    @media print {
      body { padding: 0.5in; }
    }
  </style>
</head>
<body>
  <header class="print-header">
    <h1>${B(t)}</h1>
    <p class="subtitle">A Story — Life legacy for your family</p>
    <p class="print-tagline">Printed ${be(new Date().toISOString())}</p>
  </header>
  ${r}
  <footer class="print-footer">
    <p>Every life holds a story worth preserving. — A Story</p>
  </footer>
  <script>window.onload = function() { window.print(); }<\/script>
</body>
</html>`),s.document.close()}const V="astory-session-v1",xe="astory-accounts-v1",M="demo@astory.com",j="demo1234",Qe=[{id:"dm-1",title:"The house on Maple Street",body:`We had a white clapboard house with a blue front porch. In the summer, my mother hung wind chimes from the rafters — little copper ones that sounded like rain when the breeze came through. I spent whole afternoons on that porch with a library book, listening to them sing.

The yard had a linden tree that bloomed every June. The smell of it would drift through my bedroom window at night, and to this day, if I catch that scent somewhere, I'm nine years old again.`,excerpt:"White clapboard, a blue porch, copper wind chimes that sounded like rain.",era:"Childhood",rating:5,ratingNote:"This one always makes me emotional.",photos:[],createdAt:new Date(Date.now()-288*60*60*1e3).toISOString()},{id:"dm-2",title:"Mrs. Calloway believed in me",body:`My eighth-grade English teacher, Mrs. Calloway. She pulled me aside after class one Thursday and said, "You have a voice. Don't waste it."

I was fourteen and more interested in baseball. But I've thought about that sentence nearly every day since. She saw something I couldn't see in myself yet. I don't think she ever knew how much it mattered.`,excerpt:"She pulled me aside and said: 'You have a voice. Don't waste it.'",era:"Childhood",rating:4,ratingNote:"",photos:[],createdAt:new Date(Date.now()-456*60*60*1e3).toISOString()},{id:"dm-3",title:"New Mexico, the summer of '87",body:`My college friend Pete and I drove from Chicago with nothing but a cooler, a road atlas, and three hundred dollars between us. We got lost outside Taos at dusk and ended up sleeping on the hood of the car under more stars than either of us had ever seen.

I remember lying there thinking: this is what freedom feels like. I was twenty-three. I didn't know yet how rarely you'd feel that way again.`,excerpt:"Lost outside Taos, sleeping under more stars than I'd ever seen.",era:"Early adulthood",rating:5,ratingNote:"",photos:[],createdAt:new Date(Date.now()-624*60*60*1e3).toISOString()}],Ze={onboardingComplete:!0,activeStoryId:"demo-story-walter",stories:[{id:"demo-story-walter",name:"Walter",lastName:"Morrison",relationship:"Grandfather",relationshipOther:"",birthMonth:"March",birthYear:"1945",hasAccount:"cannot",connectedEmail:"",photo:null,intro:"A man of few words but countless stories.",memories:Qe,sessionsCompleted:3,createdAt:new Date(Date.now()-720*60*60*1e3).toISOString()}]};function Xe(){try{const e=localStorage.getItem(V);return e?JSON.parse(e):null}catch{return null}}function et(e){localStorage.setItem(V,JSON.stringify(e))}function tt(){localStorage.removeItem(V)}function Ce(){try{const e=localStorage.getItem(xe);return e?JSON.parse(e):{}}catch{return{}}}function at(e){localStorage.setItem(xe,JSON.stringify(e))}function st(e,t,a,n){const r=a.toLowerCase().trim();if(r===M)return{error:"That email is already taken."};const s=Ce();return s[r]?{error:"An account with this email already exists."}:(s[r]={firstName:e.trim(),lastName:t.trim(),password:n},at(s),{ok:!0,firstName:e.trim(),lastName:t.trim()})}function nt(e,t){const a=e.toLowerCase().trim();if(a===M)return t===j?{ok:!0,firstName:"Sarah",lastName:"Morrison",isDemo:!0}:{error:"Incorrect password."};const r=Ce()[a];return r?r.password!==t?{error:"Incorrect password."}:{ok:!0,firstName:r.firstName,lastName:r.lastName}:{error:"No account found with that email."}}function Ae(){const e=`astory-state-${M}`;localStorage.getItem(e)||localStorage.setItem(e,JSON.stringify(Ze))}let f=null,k=null,y={name:"dashboard",params:{}},i=null,g={};const ot=["January","February","March","April","May","June","July","August","September","October","November","December"],ye=["The wooden porch creaked every time it rained. I can still hear her calling us in.","I was terrified — I'd never done anything like it before. But somehow I kept going.","She had this laugh that filled the whole room. You couldn't help but smile.","The smell of sawdust and coffee. That's what I remember most.","We stayed up till sunrise. I don't think I've ever felt that free since."],ge=[["Childhood","School years","Career"],["Love & family","Later years","Wisdom"],["Childhood","Career","Wisdom"],["School years","Love & family","Later years"]],h=document.getElementById("app"),c=e=>String(e??"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;"),m={home:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>',mic:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="2" width="6" height="11" rx="3"/><path d="M5 10v1a7 7 0 0 0 14 0v-1"/><path d="M12 19v3"/><path d="M8 22h8"/></svg>',tree:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22V2"/><path d="M12 8C10 8 6 7 5 4c2 0 5.5.5 7 4z"/><path d="M12 14c-2 0-6-1-7-4 2 0 5.5.5 7 4z"/><path d="M12 8c2 0 6-1 7-4-2 0-5.5.5-7 4z"/><path d="M12 14c2 0 6 1 7 4-2 0-5.5-.5-7-4z"/></svg>',menu:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><path d="M4 6h16M4 12h16M4 18h16"/></svg>',book:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>',people:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>',star:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>',chat:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>',lock:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>',settings:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>',refresh:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>',signout:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>',search:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>',faq:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><circle cx="12" cy="17" r=".5" fill="currentColor"/></svg>',mail:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>',bug:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 9v-1a4 4 0 0 0-4-4h-8a4 4 0 0 0-4 4v1"/><path d="M4 13h16"/><path d="M4 17h16"/><rect x="8" y="9" width="8" height="12" rx="2"/><path d="M8 9V7"/><path d="M16 9V7"/><path d="M2 13h2"/><path d="M20 13h2"/></svg>',idea:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18h6M10 22h4"/><path d="M12 2a7 7 0 0 1 5 11.9V17H7v-3.1A7 7 0 0 1 12 2z"/></svg>',camera:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>',user:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>',bell:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>',download:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>',chevron:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>'};function it(){const e=Xe();e!=null&&e.email?(f=e,e.email===M&&Ae(),k=Ee(e.email),U()):Le()}function U(){window.addEventListener("hashchange",ke),ke()}function A(){f&&Pe(f.email,k)}function _(e){return new Date(e).toLocaleDateString(void 0,{month:"short",day:"numeric",year:"numeric"})}function fe(){const e=new Date().getHours();return e<12?"Good morning":e<18?"Good afternoon":"Good evening"}function H(){return k.stories.find(e=>e.id===k.activeStoryId)||k.stories[0]||null}function I(e){return k.stories.find(t=>t.id===e)||null}function Te(e){return e.relationship==="Other"&&e.relationshipOther?e.relationshipOther:e.relationship}function x(e){return Ie[e.relationship]||"#d4a574"}function Me(e){return e.relationship==="Myself"}function Le(){h.innerHTML=`
    <section class="auth-splash">
      <div class="auth-splash-mark">
        <span class="auth-splash-a">A</span>
        <span class="auth-splash-word">Story</span>
      </div>
      <p class="auth-splash-tag">Every life holds a story worth preserving.</p>
    </section>`,setTimeout(J,2e3)}function J(){h.innerHTML=`
    <section class="auth-screen auth-welcome">
      <div class="auth-glow" aria-hidden="true"></div>
      <div class="auth-brand">
        <div class="auth-mark">
          <span class="auth-mark-a">A</span>
          <span class="auth-mark-word">Story</span>
        </div>
        <p class="auth-mark-tag">Every life holds a story worth preserving.</p>
      </div>
      <div class="auth-cta-group">
        <button class="btn btn--primary btn--large btn--block" id="btn-started">Get started</button>
        <button class="btn btn--ghost btn--large btn--block" id="btn-signin-go">I already have an account</button>
      </div>
      <button class="auth-demo-link" id="btn-demo">Try Walter's demo archive →</button>
    </section>`,document.getElementById("btn-started").onclick=Be,document.getElementById("btn-signin-go").onclick=qe,document.getElementById("btn-demo").onclick=()=>K(M,"Sarah","",!0)}function Be(){h.innerHTML=`
    <section class="auth-screen auth-form-screen">
      <div class="auth-glow" aria-hidden="true"></div>
      <button class="auth-back" id="auth-back">←</button>
      <div class="auth-form-top">
        <p class="auth-form-eyebrow">Create account</p>
        <h2 class="auth-form-title">Join A Story</h2>
        <p class="auth-form-sub">Start building your family's archive.</p>
      </div>
      <form class="auth-form" id="su-form" novalidate>
        <div class="auth-field-row">
          <div class="auth-field">
            <label class="auth-label" for="su-fn">First name</label>
            <input class="auth-input" id="su-fn" name="firstName" type="text"
              placeholder="First" autocomplete="given-name" required />
          </div>
          <div class="auth-field">
            <label class="auth-label" for="su-ln">Last name</label>
            <input class="auth-input" id="su-ln" name="lastName" type="text"
              placeholder="Last" autocomplete="family-name" required />
          </div>
        </div>
        <div class="auth-field">
          <label class="auth-label" for="su-em">Email</label>
          <input class="auth-input" id="su-em" name="email" type="email"
            placeholder="you@example.com" autocomplete="email" required />
        </div>
        <div class="auth-field">
          <label class="auth-label" for="su-pw">Password</label>
          <div class="auth-input-row">
            <input class="auth-input" id="su-pw" name="password" type="password"
              placeholder="Min. 6 characters" autocomplete="new-password" required minlength="6" />
            <button type="button" class="auth-reveal" data-for="su-pw"><span class="eye">👁</span></button>
          </div>
        </div>
        <div class="auth-field">
          <label class="auth-label" for="su-cp">Confirm password</label>
          <div class="auth-input-row">
            <input class="auth-input" id="su-cp" name="confirm" type="password"
              placeholder="Repeat password" autocomplete="new-password" required />
            <button type="button" class="auth-reveal" data-for="su-cp"><span class="eye">👁</span></button>
          </div>
        </div>
        <label class="auth-privacy">
          <input type="checkbox" id="su-privacy" required />
          <span>I agree to the <button type="button" class="auth-link-btn" onclick="window.showPrivacy()">Terms & Privacy Policy</button></span>
        </label>
        <p class="auth-error" id="su-err" aria-live="polite"></p>
        <button type="submit" class="btn btn--primary btn--large btn--block" id="btn-su">Create account →</button>
      </form>
      <div class="social-divider"><span>or continue with</span></div>
      <div class="social-btns">
        <button class="social-btn" onclick="alert('Google sign-in coming soon.')">
          <span class="social-icon">G</span> Google
        </button>
        <button class="social-btn" onclick="alert('Facebook sign-in coming soon.')">
          <span class="social-icon social-icon--fb">f</span> Facebook
        </button>
      </div>
      <p class="auth-toggle">Already have an account?
        <button type="button" class="auth-toggle-btn" id="go-si">Sign in</button>
      </p>
    </section>`,document.getElementById("auth-back").onclick=J,document.getElementById("go-si").onclick=qe,document.querySelectorAll(".auth-reveal").forEach(e=>{e.onclick=t=>De(t.currentTarget.dataset.for,t.currentTarget)}),document.getElementById("su-form").onsubmit=e=>{e.preventDefault();const t=new FormData(e.target),a=t.get("firstName").toString().trim(),n=t.get("lastName").toString().trim(),r=t.get("email").toString().trim(),s=t.get("password").toString(),p=t.get("confirm").toString(),w=document.getElementById("su-privacy").checked,b=document.getElementById("su-err"),C=document.getElementById("btn-su");if(!a||!n||!r){b.textContent="Please fill in all fields.";return}if(s.length<6){b.textContent="Password must be at least 6 characters.";return}if(s!==p){b.textContent="Passwords don't match.";return}if(!w){b.textContent="Please agree to the Privacy Policy.";return}C.disabled=!0,C.textContent="Creating…";const L=st(a,n,r,s);if(L.error){b.textContent=L.error,C.disabled=!1,C.textContent="Create account →";return}K(r,a,n,!1,!0)},document.getElementById("su-fn").focus()}function qe(){h.innerHTML=`
    <section class="auth-screen auth-form-screen">
      <div class="auth-glow" aria-hidden="true"></div>
      <button class="auth-back" id="auth-back">←</button>
      <div class="auth-form-top">
        <p class="auth-form-eyebrow">Welcome back</p>
        <h2 class="auth-form-title">Sign in</h2>
        <p class="auth-form-sub">Your stories are waiting.</p>
      </div>
      <form class="auth-form" id="si-form" novalidate>
        <div class="auth-field">
          <label class="auth-label" for="si-em">Email</label>
          <input class="auth-input" id="si-em" name="email" type="email"
            placeholder="you@example.com" autocomplete="email" required />
        </div>
        <div class="auth-field">
          <label class="auth-label" for="si-pw">Password</label>
          <div class="auth-input-row">
            <input class="auth-input" id="si-pw" name="password" type="password"
              placeholder="Your password" autocomplete="current-password" required />
            <button type="button" class="auth-reveal" data-for="si-pw"><span class="eye">👁</span></button>
          </div>
        </div>
        <p class="auth-error" id="si-err" aria-live="polite"></p>
        <button type="submit" class="btn btn--primary btn--large btn--block" id="btn-si">Sign in →</button>
      </form>
      <div class="social-divider"><span>or</span></div>
      <div class="social-btns">
        <button class="social-btn" onclick="alert('Google sign-in coming soon.')">
          <span class="social-icon">G</span> Google
        </button>
        <button class="social-btn" onclick="alert('Facebook sign-in coming soon.')">
          <span class="social-icon social-icon--fb">f</span> Facebook
        </button>
      </div>
      <div class="demo-card">
        <div class="demo-card-info">
          <p class="demo-card-label">Try the demo</p>
          <p class="demo-card-name">Walter's archive — 3 memories ready to explore</p>
          <p class="demo-card-creds">${M} · ${j}</p>
        </div>
        <button class="demo-card-btn" id="btn-fill">Use demo →</button>
      </div>
      <p class="auth-toggle">Don't have an account?
        <button type="button" class="auth-toggle-btn" id="go-su">Create one</button>
      </p>
    </section>`,document.getElementById("auth-back").onclick=J,document.getElementById("go-su").onclick=Be,document.getElementById("btn-fill").onclick=()=>{document.getElementById("si-em").value=M,document.getElementById("si-pw").value=j},document.querySelector(".auth-reveal").onclick=e=>De("si-pw",e.currentTarget),document.getElementById("si-form").onsubmit=e=>{e.preventDefault();const t=new FormData(e.target),a=t.get("email").toString().trim(),n=t.get("password").toString(),r=document.getElementById("si-err"),s=document.getElementById("btn-si");s.disabled=!0,s.textContent="Signing in…";const p=nt(a,n);if(p.error){r.textContent=p.error,s.disabled=!1,s.textContent="Sign in →";return}K(a,p.firstName,p.lastName,p.isDemo)},document.getElementById("si-em").focus()}function De(e,t){const a=document.getElementById(e);a.type=a.type==="password"?"text":"password",t.querySelector(".eye").textContent=a.type==="password"?"👁":"🙈"}function K(e,t,a,n,r=!1){n&&Ae(),f={email:e.toLowerCase().trim(),firstName:t,lastName:a},et(f),k=Ee(f.email),r?rt():U()}function rt(){h.innerHTML=`
    <section class="welcome-video-screen">
      <div class="auth-glow" aria-hidden="true"></div>
      <div class="wv-inner">
        <div class="wv-logo">
          <span class="auth-mark-a">A</span><span class="auth-mark-word">Story</span>
        </div>
        <p class="wv-tagline">Preserving what matters most.</p>
        <div class="wv-play-area">
          <div class="wv-play-btn" aria-hidden="true">▶</div>
          <p class="wv-play-label">Welcome video · 45 sec</p>
          <p class="wv-play-sub">Coming soon — tap Continue to explore</p>
        </div>
        <button class="btn btn--primary btn--large" id="btn-wv-continue">
          Continue →
        </button>
      </div>
    </section>`,document.getElementById("btn-wv-continue").onclick=we,setTimeout(()=>{document.getElementById("btn-wv-continue")&&we()},3500)}function we(){h.innerHTML=`
    <section class="auth-screen auth-pricing-gate">
      <div class="auth-glow" aria-hidden="true"></div>

      <div class="pg-header">
        <h2 class="pg-title">A Story</h2>
        <p class="pg-sub">Keep their voice forever</p>
      </div>

      <!-- Founding Family -->
      <div class="pricing-card pricing-card--featured">
        <div class="pricing-badge"><span class="badge-dot"></span> 47 of 100 spots remaining</div>
        <div class="pricing-card-row">
          <div>
            <h3 class="plan-name">Founding family</h3>
            <p class="plan-desc">One-time · no subscription, ever</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price">$249</span>
            <span class="plan-period">one time</span>
          </div>
        </div>
        <hr class="plan-divider" />
        <ul class="plan-features">
          <li><span class="feat-check">☐</span> Full AI interview experience</li>
          <li><span class="feat-check">☐</span> Lifetime archive access + downloads</li>
          <li><span class="feat-check">☐</span> Hardcover book included</li>
          <li><span class="feat-check">☐</span> Concierge first session</li>
          <li><span class="feat-check">☐</span> Locked rate — no future charges</li>
        </ul>
        <button class="btn plan-btn plan-btn--featured btn--large btn--block" id="pg-founding">
          Claim founding spot ↗
        </button>
      </div>

      <p class="pricing-or">or start with a 7-day free trial</p>

      <!-- Monthly -->
      <div class="pricing-card pricing-card--sub" style="margin-bottom:1rem">
        <div class="plan-toggle-row">
          <div class="plan-toggle">
            <button class="toggle-pill is-active" id="pg-monthly">Monthly</button>
            <button class="toggle-pill" id="pg-annual">Annual</button>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="pg-price">$9</span>
            <span class="plan-period" id="pg-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li><span class="feat-check">☐</span> AI interview sessions (ongoing)</li>
          <li><span class="feat-check">☐</span> Memory cards &amp; life timeline</li>
          <li><span class="feat-check">☐</span> Family archive — up to 6 members</li>
          <li><span class="feat-check">☐</span> Lifetime downloads, always free</li>
          <li><span class="feat-check">☐</span> Concierge first session</li>
        </ul>
        <div class="plan-addon">
          <div>
            <p class="addon-name">Add a hardcover book</p>
            <p class="addon-desc">Order any time, keeps forever</p>
          </div>
          <span class="addon-price">+ $69</span>
        </div>
        <button class="btn plan-btn plan-btn--sub btn--large btn--block" id="pg-trial">
          Start 7 days free ↗
        </button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>

      <button class="pg-skip" id="pg-skip">Continue for free →</button>
    </section>`,document.getElementById("pg-founding").onclick=()=>window.showComingSoon("Founding family plan"),document.getElementById("pg-trial").onclick=()=>window.showComingSoon("7-day free trial"),document.getElementById("pg-skip").onclick=()=>U(),document.getElementById("pg-monthly").onclick=()=>{document.getElementById("pg-monthly").classList.add("is-active"),document.getElementById("pg-annual").classList.remove("is-active"),document.getElementById("pg-price").textContent="$9",document.getElementById("pg-period").textContent="per month"},document.getElementById("pg-annual").onclick=()=>{document.getElementById("pg-annual").classList.add("is-active"),document.getElementById("pg-monthly").classList.remove("is-active"),document.getElementById("pg-price").textContent="$79",document.getElementById("pg-period").textContent="per year"}}function ke(){const t=(location.hash.slice(1)||"/").split("/").filter(Boolean),[a,n,r,s]=t;if(!k.onboardingComplete&&a!=="new-story"){E("new-story");return}if(a==="story"&&n){const p=r?r.split("?")[0]:"";p==="talk"?y={name:"talk",params:{storyId:n}}:p==="tree"?y={name:"tree",params:{storyId:n}}:p==="memory"?y={name:"memory",params:{storyId:n,memId:s}}:y={name:"story",params:{storyId:n}}}else switch(a){case"new-story":y={name:"new-story",params:{}};break;case"menu":y={name:"menu",params:{}};break;case"pricing":y={name:"pricing",params:{}};break;case"legacy-book":y={name:"legacy-book",params:{}};break;case"invite":y={name:"invite",params:{}};break;case"help":y={name:"help",params:{}};break;case"faq":y={name:"faq",params:{}};break;case"feedback":y={name:"feedback",params:{}};break;case"contact":y={name:"contact",params:{}};break;case"privacy":y={name:"privacy",params:{}};break;case"settings":y={name:"settings",params:{}};break;default:y={name:"dashboard",params:{}}}D()}function E(e,t={}){const a={dashboard:"#/","new-story":"#/new-story",story:`#/story/${t.storyId}`,talk:`#/story/${t.storyId}/talk`,tree:`#/story/${t.storyId}/tree`,memory:`#/story/${t.storyId}/memory/${t.memId}`,menu:"#/menu",pricing:"#/pricing","legacy-book":"#/legacy-book",invite:"#/invite",help:"#/help",faq:"#/faq",feedback:"#/feedback",contact:"#/contact",privacy:"#/privacy"};location.hash=a[e]||"#/"}function D(){switch(y.name){case"new-story":h.innerHTML=Ne();break;case"dashboard":h.innerHTML=$e();break;case"story":h.innerHTML=lt(y.params.storyId);break;case"talk":h.innerHTML=ct(y.params.storyId);break;case"tree":h.innerHTML=mt(y.params.storyId);break;case"memory":h.innerHTML=ut(y.params.storyId,y.params.memId);break;case"menu":h.innerHTML=Et();break;case"pricing":h.innerHTML=$t();break;case"legacy-book":h.innerHTML=wt();break;case"invite":h.innerHTML=kt();break;case"help":h.innerHTML=vt();break;case"faq":h.innerHTML=bt();break;case"feedback":h.innerHTML=yt();break;case"contact":h.innerHTML=gt();break;case"privacy":h.innerHTML=ft();break;case"settings":h.innerHTML=St();break;default:h.innerHTML=$e()}He()}function S(e,t){var s;const a=t||((s=H())==null?void 0:s.id)||"",n=a?`#/story/${a}/talk`:"#/new-story",r=a?`#/story/${a}/tree`:"#/";return`
    <nav class="tab-bar">
      <a href="#/" class="tab ${e==="home"?"is-active":""}">
        <span class="tab-icon">${m.home}</span><span>Home</span>
      </a>
      <a href="${n}" class="tab tab--talk ${e==="talk"?"is-active":""}">
        <span class="tab-icon tab-icon--talk">${m.mic}</span><span>Talk</span>
      </a>
      <a href="${r}" class="tab ${e==="tree"?"is-active":""}">
        <span class="tab-icon">${m.tree}</span><span>Archive</span>
      </a>
      <a href="#/menu" class="tab ${e==="menu"?"is-active":""}">
        <span class="tab-icon">${m.menu}</span><span>Menu</span>
      </a>
    </nav>`}function Ne(){const e=y.params.step||"name",t=g;if(e==="name")return`
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        ${k.stories.length>0?'<button type="button" class="btn-back" id="back-dash">← Dashboard</button>':'<div class="ob-brand"><span class="ob-brand-a">A</span><span class="ob-brand-w">Story</span></div>'}
        <h2 class="ob-q">Add a person</h2>
        <p style="color:var(--text-muted);font-size:.9375rem;margin-bottom:1.75rem">Whose life do you want to preserve?</p>
        <form class="conversational-form" id="ns-name-form">
          <div style="display:flex;gap:.75rem">
            <div style="flex:1">
              <label class="auth-label" for="ns-fn">First name</label>
              <input class="auth-input" id="ns-fn" name="firstName" type="text"
                placeholder="First" value="${c(t.name||"")}" autofocus required style="width:100%" />
            </div>
            <div style="flex:1">
              <label class="auth-label" for="ns-ln">Last name</label>
              <input class="auth-input" id="ns-ln" name="lastName" type="text"
                placeholder="Last" value="${c(t.lastName||"")}" style="width:100%" />
            </div>
          </div>
          <button type="submit" class="btn btn--primary" style="margin-top:1.5rem">Continue →</button>
        </form>
      </section>`;if(e==="relationship"){const n=t.relationship==="Other";return`
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="name">← Back</button>
        <h2 class="ob-q">How is <em>${c(t.name||"they")}</em> related to you?</h2>
        <div class="rel-grid" id="rel-grid">
          ${je.map(r=>`
            <button type="button" class="rel-btn ${t.relationship===r?"is-selected":""}"
              data-rel="${r}" style="--rel-color:${Ie[r]||"#d4a574"}">${r}</button>`).join("")}
        </div>
        <div class="rel-other-wrap ${n?"visible":""}" id="rel-other-wrap">
          <label class="auth-label" for="rel-other-input" style="margin-top:1rem;display:block">Describe the relationship</label>
          <input class="auth-input" id="rel-other-input" type="text"
            placeholder="e.g. Family friend, Mentor…"
            value="${c(t.relationshipOther||"")}" style="width:100%" />
        </div>
        <button type="button" class="btn btn--primary" id="rel-continue"
          style="margin-top:1.5rem" ${t.relationship?"":"disabled"}>Continue →</button>
      </section>`}if(e==="has-account")return`
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="relationship">← Back</button>
        <h2 class="ob-q">Does <em>${c(t.name)}</em> have an A Story account?</h2>
        <div class="account-q-cards">
          <button type="button" class="account-q-card ${t.hasAccount==="yes"?"is-sel":""}" data-acct="yes">
            <span class="aq-icon">✓</span>
            <div>
              <p class="aq-label">Yes — connect their account</p>
              <p class="aq-sub">They'll be able to see and add to their own story</p>
            </div>
          </button>
          <button type="button" class="account-q-card ${t.hasAccount==="no"?"is-sel":""}" data-acct="no">
            <span class="aq-icon">+</span>
            <div>
              <p class="aq-label">Not yet — I'll add their memories</p>
              <p class="aq-sub">You can invite them to join later</p>
            </div>
          </button>
          <button type="button" class="account-q-card ${t.hasAccount==="cannot"?"is-sel":""}" data-acct="cannot">
            <span class="aq-icon">♡</span>
            <div>
              <p class="aq-label">This person can't use the app</p>
              <p class="aq-sub">You'll preserve their story on their behalf</p>
            </div>
          </button>
        </div>
        ${t.hasAccount==="yes"?`
          <div class="aq-connect-form">
            <label class="auth-label" for="aq-email" style="margin-top:1.25rem;display:block">Their email address</label>
            <input class="auth-input" id="aq-email" type="email"
              placeholder="their@email.com" value="${c(t.connectedEmail||"")}" style="width:100%" />
            <p style="font-size:.8125rem;color:var(--text-muted);margin-top:.5rem">
              We'll send them a connection request.
            </p>
          </div>`:""}
        <button type="button" class="btn btn--primary" id="aq-continue"
          style="margin-top:1.5rem" ${t.hasAccount?"":"disabled"}>Continue →</button>
      </section>`;if(e==="dob")return`
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="${Me({relationship:t.relationship})?"relationship":"has-account"}">← Back</button>
        <h2 class="ob-q">When was <em>${c(t.name)}</em> born? <span style="font-size:1rem;color:var(--text-muted)">(optional)</span></h2>
        <form class="conversational-form" id="ns-dob-form">
          <div class="dob-row">
            <div class="dob-field">
              <label class="auth-label" for="dob-month">Month</label>
              <select class="auth-input dob-select" id="dob-month" name="month">
                <option value="">Month</option>
                ${ot.map(n=>`<option value="${n}" ${t.birthMonth===n?"selected":""}>${n}</option>`).join("")}
              </select>
            </div>
            <div class="dob-field">
              <label class="auth-label" for="dob-year">Year</label>
              <input class="auth-input" id="dob-year" name="year" type="number"
                placeholder="e.g. 1945" min="1900" max="2020"
                value="${c(t.birthYear||"")}" />
            </div>
          </div>
          <button type="submit" class="btn btn--primary" style="margin-top:1rem">Continue →</button>
          <button type="button" class="btn btn--ghost" id="dob-skip" style="margin-top:.5rem">Skip for now</button>
        </form>
      </section>`;const a=x({relationship:t.relationship});return`
    <section class="view view--full ob-screen ready-view" style="padding:2rem 1.75rem">
      <div class="ready-avatar" style="background:${a}18;border:2px solid ${a}60;color:${a}">
        ${(t.name||"?")[0].toUpperCase()}
      </div>
      <h2 class="ready-title">${c(t.name)}'s archive is ready.</h2>
      <p class="ready-sub">Every word you share becomes a memory their family can hold forever.</p>
      <div class="ready-cta">
        <button type="button" class="btn btn--primary btn--large btn--block" id="btn-create-story"
          style="background:${a}">
          Open ${c(t.name)}'s story →
        </button>
      </div>
    </section>`}function $e(){var w;const e=H(),t=new Date().toDateString(),a=((w=e.doneTopicsToday)==null?void 0:w.date)===t?e.doneTopicsToday.eras:[],r=ge[new Date().getDay()%ge.length].filter(b=>!a.includes(b));if(!e)return`
      <section class="view dashboard">
        <header class="dash-header">
          <div>
            <p class="home-greeting">${fe()}, ${c((f==null?void 0:f.firstName)||"")}</p>
            <h1 class="dash-title">Your stories</h1>
          </div>
        </header>
        <div class="dash-empty">
          <p class="dash-empty-text">No stories yet.</p>
          <p class="dash-empty-sub">Whose life do you want to preserve?</p>
        </div>
        <a href="#/new-story" class="add-story-card" style="margin-top:1rem">
          <span class="add-story-icon">+</span>
          <div>
            <p class="add-story-label">Add a person</p>
            <p class="add-story-sub">Start preserving someone's life</p>
          </div>
        </a>
      </section>
      ${S("home")}`;const s=x(e),p=[...e.memories].sort((b,C)=>new Date(C.createdAt)-new Date(b.createdAt)).slice(0,3);return`
    <section class="view dashboard">
      <!-- Story selector -->
      <header class="dash-header">
        <div class="dash-story-sel">
          <div class="dash-story-avatar" style="background:${s}18;border:1.5px solid ${s}50;color:${s}">
            ${(e.name||"?")[0].toUpperCase()}
          </div>
          <div>
            <p class="home-greeting">${fe()}, ${c((f==null?void 0:f.firstName)||"")}</p>
            <button class="dash-story-name-btn" id="btn-switch-story">
              ${c(e.name)} <span class="dash-story-chevron">▾</span>
            </button>
          </div>
        </div>
        <a href="#/new-story" class="icon-btn">+ Add</a>
      </header>

      <!-- Story switcher dropdown -->
      ${k.stories.length>1?`
        <div class="story-switcher hidden" id="story-switcher">
          ${k.stories.map(b=>`
            <button class="switcher-item ${b.id===e.id?"is-active":""}" data-switch="${b.id}">
              <span class="switcher-dot" style="background:${x(b)}"></span>
              ${c(b.name)} · ${c(Te(b))}
            </button>`).join("")}
        </div>`:""}

      <!-- Primary Talk CTA -->
      <div class="dash-talk-cta">
        <p class="dash-talk-label">What are we preserving today?</p>
        <a href="#/story/${e.id}/talk" class="dash-talk-btn" style="border-color:${s}50">
          <span class="dash-mic-icon" style="background:${s}">${m.mic}</span>
          <div>
            <p class="dash-talk-name">Talk to ${c(e.name)}'s story</p>
            <p class="dash-talk-sub">Voice · Tap to begin</p>
          </div>
        </a>
      </div>

      <!-- Daily suggestions -->
      ${r.length>0?`
      <div class="dash-suggestions">
        <p class="dash-sug-label">Today's topics</p>
        <div class="era-chips">
          ${r.map(b=>`
            <button type="button" class="era-chip" data-era="${c(b)}" data-era-sid="${e.id}">${c(b)}</button>
          `).join("")}
        </div>
      </div>`:`
      <div class="dash-suggestions">
        <p class="dash-sug-label">Today's topics</p>
        <p class="dash-topics-done">All done for today — great session. Come back tomorrow for more.</p>
      </div>`}

      <!-- Recent memories -->
      ${p.length?`
        <div class="section-head">
          <h3>Recent memories</h3>
          <a href="#/story/${e.id}/tree" class="link-sm">View all</a>
        </div>
        <div class="memory-list">
          ${p.map(b=>`
            <a href="#/story/${e.id}/memory/${b.id}" class="memory-card">
              <span class="memory-card-era" style="color:${s}">${c(b.era)}</span>
              <h4>${c(b.title)}</h4>
              <p>${c(b.excerpt)}</p>
              <div class="mc-row-bottom">
                <time>${_(b.createdAt)}</time>
                ${b.rating?`<span class="mc-stars-sm">${"★".repeat(b.rating)}${"☆".repeat(5-b.rating)}</span>`:""}
              </div>
            </a>`).join("")}
        </div>`:""}

      <!-- Keepsake book teaser -->
      <div class="keepsake-section">
        <p class="keepsake-eyebrow">A LASTING LEGACY</p>
        <h3 class="keepsake-heading">Their <em>keepsake</em></h3>
        <p class="keepsake-sub">Every answer is kept privately, in their own voice. One day, it becomes a book you can hold.</p>
        <div class="keepsake-book-wrap">
          <div class="keepsake-book">
            <div class="book-spine"></div>
            <div class="book-app-icon">A</div>
            <p class="book-serif-a">A</p>
            <p class="book-legend">A LIFE, IN THEIR WORDS</p>
          </div>
        </div>
        <div class="keepsake-stats">
          <div class="ks-stat">
            <span class="ks-num">${e.memories.length}</span>
            <span class="ks-label">MEMORIES</span>
          </div>
          <div class="ks-stat">
            <span class="ks-num">100%</span>
            <span class="ks-label">PRIVATE</span>
          </div>
          <div class="ks-stat">
            <span class="ks-num">∞</span>
            <span class="ks-label">KEPT FOREVER</span>
          </div>
        </div>
        <a href="#/legacy-book" class="btn btn--secondary btn--block ks-cta">
          Get ${c(e.name)}'s legacy book
        </a>
      </div>

      <!-- Actions -->
      <div class="dash-actions">
        <a href="#/invite" class="dash-action-btn" style="text-decoration:none">
          <span class="menu-icon">${m.people}</span> Invite family members
        </a>
      </div>
    </section>
    ${S("home",e.id)}`}function lt(e){const t=I(e);return t&&(k.activeStoryId=t.id,A()),E("dashboard"),""}function ct(e){const t=I(e);if(!t)return E("dashboard"),"";if((i==null?void 0:i.phase)==="card")return dt(t);const a=Me(t),n=ze(a,t.name),r=new URLSearchParams(location.hash.includes("?")?location.hash.split("?")[1]:"").get("era");let s;if(r){const w=n.filter(b=>b.era===r);s=w.length?w[Math.floor(Math.random()*w.length)]:n[t.sessionsCompleted%n.length]}else s=n[t.sessionsCompleted%n.length];i&&(i.storyId!==e||r&&i.prompt.era!==r)&&(i=null),i||(i={storyId:e,prompt:s,firstPerson:a,personName:t.name,phase:"first",answers:[],selectedEra:r||null,period:"",privacy:"shared"});const p=x(t);return`
    <section class="view view--chat" id="chat-view">
      <header class="chat-header">
        <button class="chat-back" id="chat-back">←</button>
        <div class="chat-identity">
          <p class="chat-ai-name">A Story</p>
          <p class="chat-era-label" style="color:${p}">${c(i.prompt.era)}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>
      <div class="chat-messages" id="chat-messages"></div>
      <footer class="chat-footer">
        <div class="chat-input-wrap">
          <button class="btn-mic-chat" id="btn-mic" title="Demo voice">${m.mic}</button>
          <textarea class="chat-textarea" id="chat-textarea"
            placeholder="Speak or type…" rows="1" maxlength="2000"></textarea>
          <button class="btn-send" id="btn-send" disabled style="background:${p}">↑</button>
        </div>
      </footer>
    </section>`}function dt(e){const t=i.draft,a=x(e);return`
    <section class="view memory-card-screen">
      <header class="chat-header">
        <button class="chat-back" id="mc-back">←</button>
        <div class="chat-identity">
          <p class="chat-ai-name">Memory Card</p>
          <p class="chat-era-label" style="color:${a}">${c(t.era)}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="mc-content">
        <div class="mc-card-preview">
          <span class="memory-card-era" style="color:${a}">${c(t.era)}</span>
          ${i.period?`<p class="mc-period">${c(i.period)}</p>`:""}
          <h3 class="mc-title" id="mc-title-el">${c(t.title)}</h3>
          <p class="mc-excerpt">${c(t.excerpt)}</p>
        </div>

        <div class="mc-rating-section">
          <p class="mc-rating-label">How satisfied are you with this memory card?</p>
          <p class="mc-rating-sub">The more details and feelings captured, the more your family will treasure it.</p>
          <div class="star-rating" id="star-rating" role="group" aria-label="Rate this memory">
            ${[1,2,3,4,5].map(n=>`
              <button type="button" class="star-btn ${(i.rating||0)>=n?"is-active":""}"
                data-star="${n}" aria-label="${n} star${n>1?"s":""}">${(i.rating||0)>=n?"★":"☆"}</button>
            `).join("")}
          </div>
          <textarea class="mc-note-input" id="mc-note" rows="2"
            placeholder="Add a note about this memory (optional)…">${c(i.ratingNote||"")}</textarea>
        </div>

        <div class="mc-media-section">
          <p class="mc-rating-label">Photos</p>
          <div class="photo-preview-grid" id="mc-photo-grid">
            ${(i.photos||[]).map((n,r)=>`
              <div class="photo-thumb">
                <img src="${n}" alt="Memory photo" />
                <button class="photo-remove" data-photo-idx="${r}" aria-label="Remove photo">×</button>
              </div>`).join("")}
          </div>
          <label class="mc-upload-btn" for="mc-photo-input">
            <span class="menu-icon">${m.camera}</span> Add photos
            <input type="file" id="mc-photo-input" accept="image/*" multiple style="display:none" />
          </label>
        </div>
      </div>

      <div class="mc-privacy-row" id="mc-privacy-row">
        <div class="mc-privacy-info">
          <p class="mc-privacy-title" id="mc-privacy-title">Shared with family</p>
          <p class="mc-privacy-sub" id="mc-privacy-desc">Family members with your invite link can view and contribute — listed as co-authors.</p>
        </div>
        <button type="button" class="mc-privacy-btn is-shared" id="btn-mc-privacy" aria-label="Toggle privacy">
          <span class="mc-privacy-btn-label" id="mc-privacy-label">Shared</span>
        </button>
      </div>

      <div class="mc-footer">
        <button type="button" class="btn btn--primary btn--block" id="btn-save-card"
          style="background:${a}">Save to archive →</button>
      </div>
    </section>`}const F=()=>document.getElementById("chat-messages"),Q=()=>{const e=F();e&&(e.scrollTop=e.scrollHeight)};function q(e){const t=F();if(!t)return;const a=document.createElement("div");a.className="msg msg--ai",a.innerHTML=`<div class="msg-avatar">A</div>
    <div class="msg-bubble">${c(e).replace(/\n/g,"<br>")}</div>`,t.appendChild(a),Q()}function pt(e){const t=F();if(!t)return;const a=document.createElement("div");a.className="msg msg--user",a.innerHTML=`<div class="msg-bubble">${c(e).replace(/\n/g,"<br>")}</div>`,t.appendChild(a),Q()}function W(){const e=F();if(!e)return;const t=document.createElement("div");t.className="msg msg--ai",t.id="thinking",t.innerHTML=`<div class="msg-avatar">A</div>
    <div class="msg-bubble thinking-dots">
      <span class="thinking-dot"></span>
      <span class="thinking-dot"></span>
      <span class="thinking-dot"></span>
    </div>`,e.appendChild(t),Q()}function R(){var e;(e=document.getElementById("thinking"))==null||e.remove()}function mt(e){const t=I(e);if(!t)return E("dashboard"),"";const a=x(t),n=N.map(s=>({era:s,items:t.memories.filter(p=>p.era===s)})).filter(s=>s.items.length),r=t.memories.filter(s=>!N.includes(s.era));return r.length&&n.push({era:"Life",items:r}),`
    <section class="view tree-view">
      <header style="margin-bottom:1.75rem">
        <p class="eyebrow" style="color:${a}">${c(t.name)}'s life</p>
        <h2 class="view-title">Memory Tree</h2>
      </header>

      ${n.length?`
        <ol class="timeline">
          ${n.map(s=>`
            <li class="timeline-era">
              <h3 style="color:${a}">${c(s.era)}</h3>
              <ul>
                ${s.items.map(p=>`
                  <li>
                    <a href="#/story/${e}/memory/${p.id}" class="timeline-item">
                      <div>
                        <strong>${c(p.title)}</strong>
                        ${p.rating?`<span class="tl-stars">${"★".repeat(p.rating)}</span>`:""}
                      </div>
                      <span>${_(p.createdAt)}</span>
                    </a>
                  </li>`).join("")}
              </ul>
            </li>`).join("")}
        </ol>`:`
        <div class="empty-state">
          <p>No memories yet.<br>Start talking to grow ${c(t.name)}'s tree.</p>
          <a href="#/story/${e}/talk" class="btn btn--primary"
            style="margin-top:1.5rem;display:inline-flex;background:${a}">Start talking</a>
        </div>`}

      <button type="button" class="dash-action-btn" style="margin-top:2rem"
        onclick="alert('Photo upload coming soon.')">
        <span class="menu-icon">${m.camera}</span> Upload photos &amp; videos
      </button>
    </section>
    ${S("tree",e)}`}function ut(e,t){const a=I(e),n=a==null?void 0:a.memories.find(s=>s.id===t);if(!a||!n)return E("dashboard"),"";const r=x(a);return`
    <section class="view memory-detail">
      <header class="detail-header">
        <a href="#/story/${e}/tree" class="btn-text">← ${c(a.name)}</a>
        <button type="button" class="btn-text btn-text--danger"
          data-del-mem="${n.id}" data-del-story="${e}">Delete</button>
      </header>
      <span class="memory-card-era" style="color:${r}">${c(n.era)}</span>
      <h1 class="detail-title">${c(n.title)}</h1>
      ${n.rating?`<p class="detail-rating">${"★".repeat(n.rating)}${"☆".repeat(5-n.rating)}</p>`:""}
      <time class="detail-date">${_(n.createdAt)}</time>
      <form class="form-stack" id="mem-form">
        <label class="field"><span>Title</span>
          <input type="text" name="title" value="${c(n.title)}" required /></label>
        <label class="field"><span>Chapter</span>
          <select name="era">
            ${N.map(s=>`<option value="${c(s)}" ${s===n.era?"selected":""}>${c(s)}</option>`).join("")}
          </select>
        </label>
        <label class="field"><span>Story</span>
          <textarea name="body" rows="10" required>${c(n.body)}</textarea>
        </label>
        <div class="field">
          <span>Photos</span>
          <div class="photo-preview-grid" id="detail-photo-grid">
            ${(n.photos||[]).map((s,p)=>`
              <div class="photo-thumb">
                <img src="${s}" alt="Memory photo" />
                <button type="button" class="photo-remove" data-detail-photo="${p}" aria-label="Remove">×</button>
              </div>`).join("")}
          </div>
          <label class="mc-upload-btn" for="detail-photo-input" style="margin-top:.5rem">
            <span class="menu-icon">${m.camera}</span> Add photos
            <input type="file" id="detail-photo-input" accept="image/*" multiple style="display:none" />
          </label>
        </div>
        <button type="submit" class="btn btn--primary" style="background:${r}">Save changes</button>
      </form>
    </section>
    ${S("tree",e)}`}const ht=[{q:"How does the AI interview work?",a:"A Story's AI acts like a warm, patient interviewer. It focuses on one topic at a time — childhood, family, career, or any chapter of life — then listens and asks a thoughtful follow-up. You can speak or type your answer. Each conversation becomes a memory card that builds your family's archive."},{q:"Is my family's data private and secure?",a:"Yes. Your stories are stored privately on your device and never shared publicly. A Story does not sell your data or use it for advertising. Your family's archive is only visible to people you personally invite."},{q:"Can I save more than one person's story?",a:"Absolutely. You can create as many story profiles as you like — one for Grandma, one for Dad, one for yourself. Each profile has its own memories, timeline, and archive, all accessible from your Home dashboard."},{q:"How do I invite family members to contribute?",a:"Tap 'Invite Family Members' on the Home screen or go to Menu → Invite Family Members. You'll get a shareable link you can send via text, email, or WhatsApp. Invited members can view memories, add their own, and upload photos."},{q:"What happens to my memories if I stop using the app?",a:"Your memories are stored locally on your device, so they stay with you. We recommend ordering a legacy book to create a permanent hardcover keepsake. Cloud backup and export features are coming soon."}];function vt(){return`
    <section class="view help-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Help &amp; Feedback</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="help-search-wrap">
        <span class="help-search-icon">${m.search}</span>
        <input class="help-search-input" type="search" placeholder="Search help topics…"
          readonly onclick="window.showComingSoon('Search')" />
      </div>

      <div class="help-section">
        <p class="help-section-label">Help Center</p>
        <a href="#/faq" class="help-item">
          <div class="help-item-icon">${m.faq}</div>
          <div class="help-item-body">
            <p class="help-item-title">Frequently Asked Questions</p>
            <p class="help-item-sub">Quick answers to common questions</p>
          </div>
          <span class="help-item-chevron">${m.chevron}</span>
        </a>
        <a href="#/contact" class="help-item">
          <div class="help-item-icon">${m.mail}</div>
          <div class="help-item-body">
            <p class="help-item-title">Contact Us</p>
            <p class="help-item-sub">Get in touch with our support team</p>
          </div>
          <span class="help-item-chevron">${m.chevron}</span>
        </a>
      </div>

      <div class="help-section">
        <p class="help-section-label">Feedback</p>
        <a href="#/feedback?type=bug" class="help-item">
          <div class="help-item-icon">${m.bug}</div>
          <div class="help-item-body">
            <p class="help-item-title">Report an issue</p>
            <p class="help-item-sub">Something not working? Let us know</p>
          </div>
          <span class="help-item-chevron">${m.chevron}</span>
        </a>
        <a href="#/feedback" class="help-item">
          <div class="help-item-icon">${m.idea}</div>
          <div class="help-item-body">
            <p class="help-item-title">Send a suggestion</p>
            <p class="help-item-sub">Share ideas to make A Story better</p>
          </div>
          <span class="help-item-chevron">${m.chevron}</span>
        </a>
      </div>

      <div class="help-section">
        <p class="help-section-label">Legal</p>
        <a href="#/privacy" class="help-item">
          <div class="help-item-icon">${m.lock}</div>
          <div class="help-item-body">
            <p class="help-item-title">Terms &amp; Privacy Policy</p>
            <p class="help-item-sub">How we handle your data</p>
          </div>
          <span class="help-item-chevron">${m.chevron}</span>
        </a>
      </div>

      <p class="help-version">A Story · Version 1.0 · Demo build</p>
    </section>
    ${S("menu")}`}function bt(){return`
    <section class="view faq-view">
      <header class="chat-header">
        <a href="#/help" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">FAQ</p>
          <p class="chat-era-label">Top questions</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="faq-list">
        ${ht.map((e,t)=>`
          <div class="faq-item">
            <button class="faq-q" data-faq="${t}" aria-expanded="false">
              <span>${c(e.q)}</span>
              <span class="faq-chevron" id="faq-ch-${t}">›</span>
            </button>
            <div class="faq-a" id="faq-a-${t}" hidden>
              <p>${c(e.a)}</p>
            </div>
          </div>`).join("")}
      </div>

      <div class="faq-footer">
        <p>Still have questions?</p>
        <a href="#/contact" class="btn btn--secondary" style="display:inline-flex;margin-top:.875rem">
          Contact us →
        </a>
      </div>
    </section>
    ${S("menu")}`}function yt(){const e=location.hash.includes("bug")?"bug":"suggestion";return`
    <section class="view feedback-view">
      <header class="chat-header">
        <a href="#/help" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Send Feedback</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="feedback-content">
        <p class="feedback-intro">We read every message. Your feedback makes A Story better for every family.</p>

        <p class="feedback-type-label">Type</p>
        <div class="feedback-type-row">
          <button class="fb-chip ${e==="bug"?"is-active":""}" data-fb-type="bug">Bug report</button>
          <button class="fb-chip ${e==="suggestion"?"is-active":""}" data-fb-type="suggestion">Suggestion</button>
          <button class="fb-chip" data-fb-type="other">Other</button>
        </div>

        <form class="form-stack" id="feedback-form" style="margin-top:1.5rem">
          <label class="field">
            <span>Your message</span>
            <textarea name="message" rows="6"
              placeholder="Describe your issue or share an idea…" required></textarea>
          </label>
          <label class="field">
            <span>Email <span style="font-weight:400;text-transform:none;letter-spacing:0">(optional — so we can follow up)</span></span>
            <input type="email" name="email" placeholder="you@example.com" autocomplete="email" />
          </label>
          <button type="submit" class="btn btn--primary btn--large btn--block">
            Send feedback →
          </button>
        </form>
      </div>
    </section>
    ${S("menu")}`}function gt(){return`
    <section class="view contact-view">
      <header class="chat-header">
        <a href="#/help" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Contact Us</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="contact-content">
        <div class="contact-info-card">
          <p class="contact-info-label">Email us</p>
          <a href="mailto:support@astoryapp.com" class="contact-email">support@astoryapp.com</a>
          <p class="contact-info-note">We usually respond within 24 hours.</p>
        </div>

        <div class="contact-divider"><span>or send a message</span></div>

        <form class="form-stack" id="contact-form">
          <label class="field">
            <span>Subject</span>
            <input type="text" name="subject" placeholder="What's this about?" />
          </label>
          <label class="field">
            <span>Message</span>
            <textarea name="message" rows="5" placeholder="How can we help?" required></textarea>
          </label>
          <label class="field">
            <span>Your email</span>
            <input type="email" name="email" placeholder="So we can reply" autocomplete="email" required />
          </label>
          <button type="submit" class="btn btn--primary btn--large btn--block">
            Send message →
          </button>
        </form>
      </div>
    </section>
    ${S("menu")}`}function ft(){return`
    <section class="view privacy-view">
      <header class="chat-header">
        <a href="#/help" class="chat-back" id="privacy-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Terms &amp; Privacy</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="privacy-content">
        <div class="privacy-hero">
          <p class="privacy-icon">${m.lock}</p>
          <h2 class="privacy-title">Terms &amp; Privacy Policy</h2>
          <p class="privacy-coming">Our legal document is being finalized.<br>We'll notify you as soon as it's ready.</p>
        </div>

        <div class="privacy-promise-card">
          <p class="promise-heading">Our commitment to you</p>
          <ul class="promise-list">
            <li>
              <span class="promise-check">✓</span>
              Your stories are stored privately on your device
            </li>
            <li>
              <span class="promise-check">✓</span>
              We never sell your data or use it for advertising
            </li>
            <li>
              <span class="promise-check">✓</span>
              Your family archive is visible only to people you invite
            </li>
            <li>
              <span class="promise-check">✓</span>
              You can delete your data at any time, permanently
            </li>
            <li>
              <span class="promise-check">✓</span>
              We will never share your stories without your consent
            </li>
          </ul>
        </div>

        <p class="privacy-notify-note">
          Want to be notified when the full document is available?
        </p>
        <button class="btn btn--secondary btn--block" onclick="window.showComingSoon('Privacy policy notification')">
          Notify me →
        </button>
      </div>
    </section>
    ${S("menu")}`}function wt(){const e=H(),t=e?x(e):"#d4a574",a=e?c(e.name):"your loved one";return`
    <section class="view lb-view">
      <header class="chat-header">
        <a href="#/" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Legacy Book</p>
          <p class="chat-era-label" style="color:${t}">${a}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="lb-hero">
        <div class="keepsake-book lb-book-sm">
          <div class="book-spine"></div>
          <div class="book-app-icon" style="font-size:1rem;width:32px;height:32px;border-radius:7px">A</div>
          <p class="book-serif-a" style="font-size:1.1rem">A</p>
          <p class="book-legend">A LIFE, IN THEIR WORDS</p>
        </div>
        <div class="lb-hero-text">
          <h2 class="lb-title">${a}'s legacy book</h2>
          <p class="lb-sub">A beautifully printed hardcover memoir — every memory, in their voice, yours to hold forever.</p>
        </div>
      </div>

      <form class="form-stack" id="lb-form">
        <label class="field">
          <span>Recipient name</span>
          <input type="text" name="fullName" placeholder="Full name" autocomplete="name" />
        </label>
        <label class="field">
          <span>Address line 1</span>
          <input type="text" name="addr1" placeholder="Street address" autocomplete="address-line1" />
        </label>
        <label class="field">
          <span>Address line 2 <span style="font-weight:400;text-transform:none;letter-spacing:0">(optional)</span></span>
          <input type="text" name="addr2" placeholder="Apt, suite, floor…" autocomplete="address-line2" />
        </label>
        <div class="lb-field-row">
          <label class="field" style="flex:2">
            <span>City</span>
            <input type="text" name="city" placeholder="City" autocomplete="address-level2" />
          </label>
          <label class="field" style="flex:1">
            <span>State</span>
            <input type="text" name="state" placeholder="State" autocomplete="address-level1" />
          </label>
        </div>
        <div class="lb-field-row">
          <label class="field" style="flex:1">
            <span>ZIP / Postal</span>
            <input type="text" name="zip" placeholder="ZIP" autocomplete="postal-code" inputmode="numeric" />
          </label>
          <label class="field" style="flex:1.5">
            <span>Country</span>
            <input type="text" name="country" placeholder="Country" autocomplete="country-name" value="United States" />
          </label>
        </div>
        <button type="submit" class="btn btn--primary btn--large btn--block" style="background:${t}">
          Place order →
        </button>
      </form>
    </section>
    ${S("home")}`}function kt(){const e=H(),t=e?x(e):"#d4a574",a=e?c(e.name):"the story",r=`https://astory.app/join/${((e==null?void 0:e.id)||"family").replace(/-/g,"").slice(0,8).toUpperCase()}`;return`
    <section class="view invite-view">
      <header class="chat-header">
        <a href="#/" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Invite Family</p>
          <p class="chat-era-label" style="color:${t}">${a}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="invite-content">
        <h2 class="invite-title">Invite family to contribute</h2>
        <p class="invite-sub">Family members can view memories, add their own, and upload photos.</p>

        <div class="invite-link-card">
          <p class="invite-link-label">Shareable link</p>
          <div class="invite-link-row">
            <code class="invite-link-text" id="invite-link-text">${r}</code>
            <button class="invite-copy-btn" id="btn-copy-link" type="button">Copy</button>
          </div>
        </div>

        <div class="invite-divider"><span>or send directly</span></div>

        <form class="form-stack" id="invite-form">
          <label class="field">
            <span>Email address</span>
            <input type="email" name="email" placeholder="family@example.com"
              autocomplete="email" inputmode="email" />
          </label>
          <label class="field">
            <span>Phone number <span style="font-weight:400;text-transform:none;letter-spacing:0">(optional)</span></span>
            <input type="tel" name="phone" placeholder="+1 (555) 000-0000"
              autocomplete="tel" inputmode="tel" />
          </label>
          <button type="submit" class="btn btn--primary btn--large btn--block" style="background:${t}">
            Send invite link
          </button>
        </form>

        <p class="invite-note">
          They'll be able to see ${a}'s memories and add their own perspective.
        </p>
      </div>
    </section>
    ${S("home")}`}window.showPrivacy=function(){var t;(t=document.getElementById("cs-modal"))==null||t.remove();const e=document.createElement("div");e.id="cs-modal",e.className="cs-overlay",e.innerHTML=`
    <div class="cs-modal">
      <p class="cs-icon">${m.lock}</p>
      <h3 class="cs-title">Terms &amp; Privacy Policy</h3>
      <p class="cs-body">Our legal document is being finalized. We commit to never selling your data, sharing your stories without consent, or using your information for advertising.</p>
      <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Got it</button>
    </div>`,document.body.appendChild(e),e.onclick=a=>{a.target===e&&e.remove()}};window.showComingSoon=function(e="This feature"){var a;(a=document.getElementById("cs-modal"))==null||a.remove();const t=document.createElement("div");t.id="cs-modal",t.className="cs-overlay",t.innerHTML=`
    <div class="cs-modal" role="dialog" aria-modal="true">
      <p class="cs-icon">${m.star}</p>
      <h3 class="cs-title">${e}</h3>
      <p class="cs-body">We're working on something beautiful. This will be ready soon.</p>
      <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Got it</button>
    </div>`,document.body.appendChild(t),t.onclick=n=>{n.target===t&&t.remove()}};function $t(){return`
    <section class="view pricing-view">
      <header class="pricing-header-bar">
        <a href="#/" class="chat-back">←</a>
        <div class="pricing-header-title">
          <h2 class="pricing-brand">A Story</h2>
          <p class="pricing-brand-sub">Keep their voice forever</p>
        </div>
        <div style="width:44px"></div>
      </header>

      <!-- Founding Family -->
      <div class="pricing-card pricing-card--featured">
        <div class="pricing-badge">
          <span class="badge-dot"></span> 47 of 100 spots remaining
        </div>
        <div class="pricing-card-row">
          <div>
            <h3 class="plan-name">Founding family</h3>
            <p class="plan-desc">One-time · no subscription, ever</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price">$249</span>
            <span class="plan-period">one time</span>
          </div>
        </div>
        <hr class="plan-divider" />
        <ul class="plan-features">
          <li><span class="feat-check">☐</span> Full AI interview experience</li>
          <li><span class="feat-check">☐</span> Lifetime archive access + downloads</li>
          <li><span class="feat-check">☐</span> Hardcover book included</li>
          <li><span class="feat-check">☐</span> Concierge first session</li>
          <li><span class="feat-check">☐</span> Locked rate — no future charges</li>
        </ul>
        <button class="btn plan-btn plan-btn--featured btn--large btn--block"
          onclick="window.showComingSoon('Founding family plan')">
          Claim founding spot ↗
        </button>
      </div>

      <p class="pricing-or">or start with a 7-day free trial</p>

      <!-- Monthly / Annual -->
      <div class="pricing-card pricing-card--sub">
        <div class="plan-toggle-row">
          <div class="plan-toggle">
            <button class="toggle-pill is-active" id="toggle-monthly">Monthly</button>
            <button class="toggle-pill" id="toggle-annual">Annual</button>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="sub-price">$9</span>
            <span class="plan-period" id="sub-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li><span class="feat-check">☐</span> AI interview sessions (ongoing)</li>
          <li><span class="feat-check">☐</span> Memory cards &amp; life timeline</li>
          <li><span class="feat-check">☐</span> Family archive — up to 6 members</li>
          <li><span class="feat-check">☐</span> Lifetime downloads, always free</li>
          <li><span class="feat-check">☐</span> Concierge first session</li>
        </ul>
        <div class="plan-addon">
          <div>
            <p class="addon-name">Add a hardcover book</p>
            <p class="addon-desc">Order any time, keeps forever</p>
          </div>
          <span class="addon-price">+ $69</span>
        </div>
        <button class="btn plan-btn plan-btn--sub btn--large btn--block"
          onclick="window.showComingSoon('7-day free trial')">
          Start 7 days free ↗
        </button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>
    </section>
    ${S("menu")}`}function St(){const e=f;return`
    <section class="view settings-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Settings</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="settings-section">
        <p class="settings-section-title">Account</p>
        <div class="settings-item">
          <div class="settings-item-icon">${m.user}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Display name</p>
            <p class="settings-item-value">${c((e==null?void 0:e.firstName)||"")} ${c((e==null?void 0:e.lastName)||"")}</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Change name')">Edit</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${m.mail}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Email address</p>
            <p class="settings-item-value">${c((e==null?void 0:e.email)||"")}</p>
          </div>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${m.lock}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Password</p>
            <p class="settings-item-value settings-item-value--muted">••••••••</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Change password')">Change</button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Interview</p>
        <div class="settings-item">
          <div class="settings-item-icon">${m.chat}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Language</p>
            <p class="settings-item-value">Auto-detect</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Language settings')">Change</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${m.mic}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Voice input</p>
            <p class="settings-item-value">Tap mic to record</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Voice settings')">Set up</button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Notifications</p>
        <div class="settings-item">
          <div class="settings-item-icon">${m.bell}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Daily reminder</p>
            <p class="settings-item-value">Off</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Notifications')">Enable</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${m.bell}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Family activity</p>
            <p class="settings-item-value">Off</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Family notifications')">Enable</button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Your data</p>
        <div class="settings-item">
          <div class="settings-item-icon">${m.download}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Export archive</p>
            <p class="settings-item-value">Download all stories as PDF</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Export archive')">Export</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${m.lock}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Storage</p>
            <p class="settings-item-value">All data stored locally on this device</p>
          </div>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">About</p>
        <div class="settings-item settings-item--static">
          <div class="settings-item-body">
            <p class="settings-item-label">Version</p>
            <p class="settings-item-value">1.0 · Demo build</p>
          </div>
        </div>
        <div class="settings-item settings-item--static">
          <div class="settings-item-body">
            <p class="settings-item-label">Rate A Story</p>
            <p class="settings-item-value">Share your experience</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Rate A Story')">Rate</button>
        </div>
      </div>

      <p class="fine-print" style="margin-bottom:2rem">A Story · Version 1.0</p>
    </section>
    ${S("menu")}`}function Et(){const e=H();return`
    <section class="view menu-view">
      <div class="menu-profile-card">
        <div class="menu-avatar">
          ${((f==null?void 0:f.firstName)||"?")[0].toUpperCase()}
        </div>
        <div>
          <p class="menu-name">${c((f==null?void 0:f.firstName)||"")} ${c((f==null?void 0:f.lastName)||"")}</p>
          <p class="menu-email">${c((f==null?void 0:f.email)||"")}</p>
        </div>
      </div>

      <div class="menu-section">
        <p class="menu-section-title">Your stories</p>
        ${k.stories.map(t=>`
          <a href="#/story/${t.id}" class="menu-item">
            <span class="menu-item-dot" style="background:${x(t)}"></span>
            <span>${c(t.name)} · ${c(Te(t))}</span>
            <span class="menu-item-arrow">→</span>
          </a>`).join("")}
        <a href="#/new-story" class="menu-item">
          <span class="menu-item-dot" style="background:var(--border-mid)">+</span>
          <span>Add a person</span>
        </a>
      </div>

      ${e?`
        <div class="menu-section">
          <p class="menu-section-title">Archive</p>
          <a href="#/legacy-book" class="menu-item">
            <span class="menu-icon">${m.book}</span><span>Get ${c(e.name)}'s legacy book</span>
            <span class="menu-item-arrow">${m.chevron}</span>
          </a>
          <a href="#/invite" class="menu-item">
            <span class="menu-icon">${m.people}</span><span>Invite family members</span>
            <span class="menu-item-arrow">${m.chevron}</span>
          </a>
        </div>`:""}

      <div class="menu-section">
        <p class="menu-section-title">Plans</p>
        <a href="#/pricing" class="menu-item">
          <span class="menu-icon">${m.star}</span><span>Get A Story Premium</span>
          <span class="menu-item-arrow">${m.chevron}</span>
        </a>
      </div>

      <div class="menu-section">
        <p class="menu-section-title">Account</p>
        <a href="#/settings" class="menu-item">
          <span class="menu-icon">${m.settings}</span><span>Settings</span>
          <span class="menu-item-arrow">${m.chevron}</span>
        </a>
        <a href="#/help" class="menu-item">
          <span class="menu-icon">${m.chat}</span><span>Help &amp; Feedback</span>
          <span class="menu-item-arrow">${m.chevron}</span>
        </a>
        <a href="#/privacy" class="menu-item">
          <span class="menu-icon">${m.lock}</span><span>Terms &amp; Privacy Policy</span>
          <span class="menu-item-arrow">${m.chevron}</span>
        </a>
      </div>

      <div class="menu-section">
        <button type="button" class="menu-item menu-item--danger" data-reset>
          <span class="menu-icon">${m.refresh}</span><span>Reset all data</span>
        </button>
        <button type="button" class="menu-item menu-item--danger" data-signout>
          <span class="menu-icon">${m.signout}</span><span>Sign out</span>
        </button>
      </div>
      <p class="fine-print">All memories are stored locally on this device.</p>
    </section>
    ${S("menu")}`}function He(){var a,n,r,s,p,w,b,C,L,Z,X,ee,te,ae,se,ne,oe,ie,re,le,ce,de,pe;(a=document.getElementById("back-dash"))==null||a.addEventListener("click",()=>E("dashboard")),(n=document.getElementById("ns-name-form"))==null||n.addEventListener("submit",o=>{var d,u;o.preventDefault();const l=new FormData(o.target);g.name=(d=l.get("firstName"))==null?void 0:d.toString().trim(),g.lastName=(u=l.get("lastName"))==null?void 0:u.toString().trim(),g.name&&T("relationship")}),h.querySelectorAll("[data-rel]").forEach(o=>{o.onclick=()=>{var u;h.querySelectorAll("[data-rel]").forEach(v=>v.classList.remove("is-selected")),o.classList.add("is-selected"),g.relationship=o.dataset.rel;const l=document.getElementById("rel-other-wrap"),d=document.getElementById("rel-continue");o.dataset.rel==="Other"?(l==null||l.classList.add("visible"),(u=document.getElementById("rel-other-input"))==null||u.focus()):(l==null||l.classList.remove("visible"),g.relationshipOther=""),d&&(d.disabled=!1)}}),(r=document.getElementById("rel-continue"))==null||r.addEventListener("click",()=>{var l,d;if(!g.relationship)return;if(g.relationship==="Other"&&(g.relationshipOther=((l=document.getElementById("rel-other-input"))==null?void 0:l.value.trim())||"",!g.relationshipOther)){(d=document.getElementById("rel-other-input"))==null||d.focus();return}const o=g.relationship==="Myself"?"dob":"has-account";T(o)}),h.querySelectorAll("[data-acct]").forEach(o=>{o.onclick=()=>{g.hasAccount=o.dataset.acct,T("has-account")}}),(s=document.getElementById("aq-continue"))==null||s.addEventListener("click",()=>{var o;g.hasAccount&&(g.hasAccount==="yes"&&(g.connectedEmail=((o=document.getElementById("aq-email"))==null?void 0:o.value.trim())||""),T("dob"))}),(p=document.getElementById("ns-dob-form"))==null||p.addEventListener("submit",o=>{var d,u;o.preventDefault();const l=new FormData(o.target);g.birthMonth=((d=l.get("month"))==null?void 0:d.toString())||"",g.birthYear=((u=l.get("year"))==null?void 0:u.toString().trim())||"",T("done")}),(w=document.getElementById("dob-skip"))==null||w.addEventListener("click",()=>T("done")),document.querySelectorAll("[data-ns-back]").forEach(o=>{o.onclick=()=>T(o.dataset.nsBack)}),(b=document.getElementById("btn-create-story"))==null||b.addEventListener("click",()=>{const o=We({name:g.name||"",lastName:g.lastName||"",relationship:g.relationship||"Relative",relationshipOther:g.relationshipOther||"",birthMonth:g.birthMonth||"",birthYear:g.birthYear||"",hasAccount:g.hasAccount||null,connectedEmail:g.connectedEmail||""});k.stories.push(o),k.activeStoryId=o.id,k.onboardingComplete=!0,g={},A(),E("dashboard")}),(C=document.getElementById("btn-switch-story"))==null||C.addEventListener("click",()=>{var o;(o=document.getElementById("story-switcher"))==null||o.classList.toggle("hidden")}),h.querySelectorAll("[data-switch]").forEach(o=>{o.onclick=()=>{k.activeStoryId=o.dataset.switch,A(),D()}}),h.querySelectorAll("[data-era]").forEach(o=>{o.onclick=()=>{i=null;const l=o.dataset.eraSid;location.hash=`#/story/${l}/talk?era=${encodeURIComponent(o.dataset.era)}`}}),h.querySelectorAll("[data-print]").forEach(o=>{o.onclick=()=>{const l=I(o.dataset.print);l&&Ke({...k,memories:l.memories,profile:{name:l.name}})}}),(L=document.getElementById("mem-form"))==null||L.addEventListener("submit",o=>{var O,me,ue;o.preventDefault();const{storyId:l,memId:d}=y.params,u=I(l),v=u==null?void 0:u.memories.find(Oe=>Oe.id===d);if(!u||!v)return;const $=new FormData(o.target);v.title=((O=$.get("title"))==null?void 0:O.toString().trim())||v.title,v.era=((me=$.get("era"))==null?void 0:me.toString())||v.era,v.body=((ue=$.get("body"))==null?void 0:ue.toString().trim())||v.body,v.excerpt=v.body.length>160?`${v.body.slice(0,157)}…`:v.body,A(),D()}),(Z=h.querySelector("[data-del-mem]"))==null||Z.addEventListener("click",o=>{const{storyId:l}=y.params,d=I(l);!d||!confirm("Delete this memory permanently?")||(d.memories=d.memories.filter(u=>u.id!==o.target.dataset.delMem),A(),E("tree",{storyId:l}))}),h.querySelectorAll("[data-star]").forEach(o=>{o.onclick=()=>{const l=parseInt(o.dataset.star);i&&(i.rating=l),h.querySelectorAll("[data-star]").forEach(d=>{const u=parseInt(d.dataset.star);d.textContent=u<=l?"★":"☆",d.classList.toggle("is-active",u<=l)})}}),(X=document.getElementById("btn-mc-privacy"))==null||X.addEventListener("click",()=>{if(!i)return;const o=i.privacy!=="private";i.privacy=o?"private":"shared";const l=document.getElementById("btn-mc-privacy"),d=document.getElementById("mc-privacy-title"),u=document.getElementById("mc-privacy-desc"),v=document.getElementById("mc-privacy-label");i.privacy==="private"?(l.classList.remove("is-shared"),l.classList.add("is-private"),v.textContent="Private",d.textContent="Private memory",u.textContent="Only you can see this memory."):(l.classList.remove("is-private"),l.classList.add("is-shared"),v.textContent="Shared",d.textContent="Shared with family",u.textContent="Family members with your invite link can view and contribute — listed as co-authors.")}),(ee=document.getElementById("btn-save-card"))==null||ee.addEventListener("click",()=>{var u;if(!i)return;const o=I(i.storyId);if(!o)return;const l=((u=document.getElementById("mc-note"))==null?void 0:u.value.trim())||"",d=Re({...i.draft,rating:i.rating||0,ratingNote:l,period:i.period||"",privacy:i.privacy||"shared"});if(d.photos=i.photos||[],o.memories.unshift(d),o.sessionsCompleted+=1,i.selectedEra){const v=new Date().toDateString();(!o.doneTopicsToday||o.doneTopicsToday.date!==v)&&(o.doneTopicsToday={date:v,eras:[]}),o.doneTopicsToday.eras.includes(i.selectedEra)||o.doneTopicsToday.eras.push(i.selectedEra)}A(),i=null,E("tree",{storyId:o.id})}),(te=document.getElementById("mc-back"))==null||te.addEventListener("click",()=>{i&&(i.phase="followup"),D()}),(ae=h.querySelector("[data-reset]"))==null||ae.addEventListener("click",()=>{confirm("Reset all data? Cannot be undone.")&&(k=Fe(f.email),i=null,g={},E("new-story"))}),(se=document.getElementById("mc-photo-input"))==null||se.addEventListener("change",o=>{i&&(i.photos||(i.photos=[]),Se(Array.from(o.target.files),l=>{i.photos.push(...l),Y()}))}),h.querySelectorAll("[data-photo-idx]").forEach(o=>{o.onclick=()=>{const l=parseInt(o.dataset.photoIdx);i!=null&&i.photos&&(i.photos.splice(l,1),Y())}}),(ne=document.getElementById("detail-photo-input"))==null||ne.addEventListener("change",o=>{const{storyId:l,memId:d}=y.params,u=I(l),v=u==null?void 0:u.memories.find($=>$.id===d);v&&(v.photos||(v.photos=[]),Se(Array.from(o.target.files),$=>{v.photos.push(...$),A(),z(v)}))}),h.querySelectorAll("[data-detail-photo]").forEach(o=>{o.onclick=()=>{const{storyId:l,memId:d}=y.params,u=I(l),v=u==null?void 0:u.memories.find(O=>O.id===d);if(!v)return;const $=parseInt(o.dataset.detailPhoto);v.photos.splice($,1),A(),z(v)}}),h.querySelectorAll("[data-faq]").forEach(o=>{o.onclick=()=>{const l=o.dataset.faq,d=document.getElementById(`faq-a-${l}`),u=document.getElementById(`faq-ch-${l}`),v=!d.hidden;h.querySelectorAll(".faq-a").forEach($=>{$.hidden=!0}),h.querySelectorAll(".faq-chevron").forEach($=>{$.textContent="›",$.classList.remove("open")}),h.querySelectorAll(".faq-q").forEach($=>$.setAttribute("aria-expanded","false")),v||(d.hidden=!1,u&&(u.textContent="⌄",u.classList.add("open")),o.setAttribute("aria-expanded","true"))}}),h.querySelectorAll("[data-fb-type]").forEach(o=>{o.onclick=()=>{h.querySelectorAll("[data-fb-type]").forEach(l=>l.classList.remove("is-active")),o.classList.add("is-active")}}),(oe=document.getElementById("feedback-form"))==null||oe.addEventListener("submit",o=>{o.preventDefault();const l=o.target.querySelector("button[type=submit]");l.disabled=!0,l.textContent="Sending…",setTimeout(()=>{var u;l.disabled=!1,l.textContent="Send feedback →",o.target.reset(),h.querySelectorAll("[data-fb-type]").forEach((v,$)=>v.classList.toggle("is-active",$===0)),(u=document.getElementById("cs-modal"))==null||u.remove();const d=document.createElement("div");d.id="cs-modal",d.className="cs-overlay",d.innerHTML=`
        <div class="cs-modal">
          <p class="cs-icon">${m.mail}</p>
          <h3 class="cs-title">Feedback sent</h3>
          <p class="cs-body">Thank you — we read every message and use it to make A Story better.</p>
          <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Done</button>
        </div>`,document.body.appendChild(d),d.onclick=v=>{v.target===d&&d.remove()}},800)}),(ie=document.getElementById("contact-form"))==null||ie.addEventListener("submit",o=>{o.preventDefault();const l=o.target.querySelector("button[type=submit]");l.disabled=!0,l.textContent="Sending…",setTimeout(()=>{var u;l.disabled=!1,l.textContent="Send message →",o.target.reset(),(u=document.getElementById("cs-modal"))==null||u.remove();const d=document.createElement("div");d.id="cs-modal",d.className="cs-overlay",d.innerHTML=`
        <div class="cs-modal">
          <p class="cs-icon">${m.mail}</p>
          <h3 class="cs-title">Message sent</h3>
          <p class="cs-body">We'll get back to you at your email within 24 hours.</p>
          <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Done</button>
        </div>`,document.body.appendChild(d),d.onclick=v=>{v.target===d&&d.remove()}},800)}),(re=document.getElementById("privacy-back"))==null||re.addEventListener("click",o=>{f||(o.preventDefault(),history.back())}),(le=document.getElementById("lb-form"))==null||le.addEventListener("submit",o=>{o.preventDefault(),window.showComingSoon("Legacy book ordering")}),(ce=document.getElementById("invite-form"))==null||ce.addEventListener("submit",o=>{o.preventDefault(),window.showComingSoon("Sending family invites")}),(de=document.getElementById("btn-copy-link"))==null||de.addEventListener("click",()=>{var d,u;const o=((d=document.getElementById("invite-link-text"))==null?void 0:d.textContent)||"",l=document.getElementById("btn-copy-link");(u=navigator.clipboard)==null||u.writeText(o).then(()=>{l&&(l.textContent="Copied!",l.classList.add("copied")),setTimeout(()=>{l&&(l.textContent="Copy",l.classList.remove("copied"))},2500)}).catch(()=>{l&&(l.textContent="Copy the link above")})});const e=document.getElementById("toggle-monthly"),t=document.getElementById("toggle-annual");e&&t&&(e.onclick=()=>{e.classList.add("is-active"),t.classList.remove("is-active"),document.getElementById("sub-price").textContent="$9",document.getElementById("sub-period").textContent="per month"},t.onclick=()=>{t.classList.add("is-active"),e.classList.remove("is-active"),document.getElementById("sub-price").textContent="$79",document.getElementById("sub-period").textContent="per year"}),(pe=h.querySelector("[data-signout]"))==null||pe.addEventListener("click",()=>{confirm("Sign out?")&&(tt(),i=null,g={},f=null,k=null,location.hash="",Le())}),It()}function T(e){y.name="new-story",y.params={step:e},h.innerHTML=Ne(),He()}function Se(e,t){const a=[];e.length&&e.forEach(n=>{const r=new FileReader;r.onload=s=>{a.push(s.target.result),a.length===e.length&&t(a)},r.readAsDataURL(n)})}function Y(){const e=document.getElementById("mc-photo-grid");!e||!i||(e.innerHTML=(i.photos||[]).map((t,a)=>`
    <div class="photo-thumb">
      <img src="${t}" alt="Memory photo" />
      <button class="photo-remove" data-photo-idx="${a}" aria-label="Remove photo">×</button>
    </div>`).join(""),e.querySelectorAll("[data-photo-idx]").forEach(t=>{t.onclick=()=>{const a=parseInt(t.dataset.photoIdx);i!=null&&i.photos&&(i.photos.splice(a,1),Y())}}))}function z(e){const t=document.getElementById("detail-photo-grid");t&&(t.innerHTML=(e.photos||[]).map((a,n)=>`
    <div class="photo-thumb">
      <img src="${a}" alt="Memory photo" />
      <button type="button" class="photo-remove" data-detail-photo="${n}" aria-label="Remove">×</button>
    </div>`).join(""),t.querySelectorAll("[data-detail-photo]").forEach(a=>{a.onclick=()=>{const n=parseInt(a.dataset.detailPhoto);e.photos.splice(n,1),A(),z(e)}}))}function It(){const e=document.getElementById("chat-textarea"),t=document.getElementById("btn-send"),a=document.getElementById("chat-back"),n=document.getElementById("btn-mic");if(!e||!t)return;a==null||a.addEventListener("click",()=>{const s=i==null?void 0:i.storyId;i=null,E("dashboard")}),setTimeout(()=>{if(!i)return;const s=i.personName,p=i.firstPerson?`Hello — I'm so glad you're here.

I'm here to listen and help preserve your story. There are no rules, no order, no right or wrong answers. You're in complete control — share whatever feels meaningful, and stop or pause whenever you like.

Whenever you're ready... ${i.prompt.question}`:`Hello — I'm so glad you're here.

I'm here to help you capture and preserve ${s}'s story — the memories, the moments, the details that matter. There are no rules and no order. Share whatever comes to mind, and stop whenever you like.

Whenever you're ready... ${i.prompt.question}`;q(p)},380),e.addEventListener("input",()=>{e.style.height="auto",e.style.height=Math.min(e.scrollHeight,120)+"px",t.disabled=!e.value.trim()}),e.addEventListener("keydown",s=>{s.key==="Enter"&&!s.shiftKey&&(s.preventDefault(),t.disabled||r())}),t.addEventListener("click",r),n==null||n.addEventListener("click",()=>{e.value.trim()||(e.value=ye[Math.floor(Math.random()*ye.length)],e.dispatchEvent(new Event("input")),e.focus())});function r(){const s=e.value.trim();!s||!i||i.phase==="done"||i.phase==="card"||(pt(s),e.value="",e.style.height="auto",t.disabled=!0,e.disabled=!0,e.placeholder="A Story is listening…",i.answers.push(s),i.phase==="first"?(i.phase="followup",W(),setTimeout(()=>{R(),q(Ge(s,i.prompt.followUp)),e.disabled=!1,e.placeholder="Keep going…",e.focus()},1100+Math.random()*500)):i.phase==="followup"?(i.phase="period",W(),setTimeout(()=>{R(),q(_e(i.firstPerson,i.personName,i.prompt.era)),e.disabled=!1,e.placeholder="e.g. Around 1978, mid-50s…",e.focus()},900+Math.random()*400)):i.phase==="period"&&(i.period=s,i.phase="reflecting",W(),setTimeout(()=>{R(),q(Ve(i.answers,i.firstPerson,i.personName)),setTimeout(()=>{q("I have a beautiful picture of this memory. Let's preserve it as a memory card."),setTimeout(()=>{i.draft=Je(i.prompt,i.answers),i.phase="card",i.rating=0,D()},1200)},600)},1600+Math.random()*600)))}}it();
