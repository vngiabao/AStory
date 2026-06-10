import { chromium } from 'playwright';

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage();
await page.setViewportSize({ width: 390, height: 844 });

// Visit app, wait past splash
await page.goto('http://localhost:5173/');
await page.waitForTimeout(3000);
await page.screenshot({ path: 'C:/Users/nguye/Desktop/a-story-app/screen1_welcome.png' });
console.log('1. Welcome screen captured');

// Sign in
await page.click('#btn-signin-go');
await page.waitForTimeout(500);
await page.fill('#si-em', 'demo@astory.com');
await page.fill('#si-pw', 'demo1234');
await page.click('#btn-si');
await page.waitForTimeout(2000);
await page.screenshot({ path: 'C:/Users/nguye/Desktop/a-story-app/screen3_dashboard.png' });
console.log('2. Dashboard captured');

// Check tab bar HTML
const tabBar = await page.$eval('.tab-bar', el => el.innerHTML);
const hasSVG = tabBar.includes('<svg');
const hasEmoji = tabBar.includes('⌂') || tabBar.includes('🎙') || tabBar.includes('✶') || tabBar.includes('≡');
console.log('Tab bar has SVG:', hasSVG, '| Has old emoji:', hasEmoji);

// Navigate to Menu
await page.click('a[href="#/menu"]');
await page.waitForTimeout(500);
await page.screenshot({ path: 'C:/Users/nguye/Desktop/a-story-app/screen4_menu.png' });

// Check for Settings in menu
const menuText = await page.$eval('.menu-view', el => el.innerText);
const hasSettings = menuText.includes('Settings');
console.log('Menu has Settings:', hasSettings);

// Navigate to Settings
await page.click('a[href="#/settings"]');
await page.waitForTimeout(500);
await page.screenshot({ path: 'C:/Users/nguye/Desktop/a-story-app/screen5_settings.png' });
const settingsText = await page.$eval('.settings-view', el => el.innerText);
console.log('Settings has Account:', settingsText.includes('Account'));
console.log('Settings has Interview:', settingsText.includes('Interview'));
console.log('Settings has Notifications:', settingsText.includes('Notifications'));
console.log('Settings has Your data:', settingsText.includes('Your data'));
console.log('Settings has About:', settingsText.includes('About'));

// Navigate to Talk (AI interview) — get storyId from URL
await page.goto('http://localhost:5173/');
await page.waitForTimeout(500);
const talkHref = await page.$eval('.tab--talk', el => el.getAttribute('href'));
await page.goto('http://localhost:5173/' + talkHref);
await page.waitForTimeout(1500);
await page.screenshot({ path: 'C:/Users/nguye/Desktop/a-story-app/screen6_chat.png' });
const firstAIMsg = await page.$eval('.msg--ai .msg-bubble', el => el.innerText).catch(() => 'NO MSG FOUND');
console.log('AI opening (first 120 chars):', firstAIMsg.substring(0, 120));
console.log('Has warm greeting:', firstAIMsg.includes("I'm so glad you're here") || firstAIMsg.includes("I'm here to listen"));

await browser.close();
console.log('Done.');
