// BookRecommendation 前端截图脚本（容器内以 puppeteer 访问 compose 网络的 frontend 服务）
// 输出目录：/shots（默认，可由环境变量 OUT_DIR 覆盖）
const puppeteer = require('puppeteer');
const path = require('path');
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

(async () => {
  const outDir = process.env.OUT_DIR || '/shots';
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-gpu', '--disable-dev-shm-usage', '--hide-scrollbars', '--lang=zh-CN'],
  });
  const page = await browser.newPage();
  await page.setViewport({ width: 1920, height: 1080, deviceScaleFactor: 1 });
  const BASE = 'http://frontend';

  // 网络诊断：打印登录接口的响应与失败请求
  page.on('response', async (r) => {
    if (r.url().includes('/login') || r.url().includes('login')) {
      let body = '';
      try { body = (await r.text()).slice(0, 200); } catch (_) { /* ignore */ }
      console.log('LOGIN RESP', r.status(), JSON.stringify(body));
    }
  });
  page.on('requestfailed', (req) => {
    const f = req.failure();
    console.log('REQ FAILED', req.url(), f ? f.errorText : '');
  });
  const shot = async (name, ms = 2500) => {
    await sleep(ms);
    await page.screenshot({ path: path.join(outDir, name) });
    console.log('saved', name);
  };

  // 1) 登录页（等待可能的"请先登录"提示自动消失后再截，保持画面干净）
  await page.goto(`${BASE}/#/Login`, { waitUntil: 'networkidle0', timeout: 90000 });
  await page.waitForSelector('.login_form input', { timeout: 60000 });
  await page.waitForFunction(() => !document.querySelector('.el-message'), { timeout: 10000 }).catch(() => {});
  await shot('login.png', 1500);

  // 2) 登录：直接调 API + 按前端逻辑写入 sessionStorage（规避 UI 点击时序问题）
  const api = await page.evaluate(async () => {
    const base = '/book_recommendation';
    const login = await fetch(base + '/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account: '2020001', password: '123456', username: '', time: '', cert_id: '', state: 0, head: '' }),
    });
    const j = await login.json();
    const token = login.headers.get('authorization');
    sessionStorage.setItem('token', token);
    const me = await fetch(base + '/sys/user/selectByAccount?account=2020001', {
      headers: { Authorization: token },
    });
    const mj = await me.json();
    sessionStorage.setItem('loginForm', JSON.stringify(mj.data));
    return { code: j.code, token: !!token, user: mj.data && mj.data.account };
  });
  console.log('API login:', JSON.stringify(api));
  if (!api.token) throw new Error('API 登录未返回 token');
  await sleep(1000);

  // 3) 首页（用户卡片 + 借阅提醒 + ECharts 图表）
  await page.goto(`${BASE}/#/Home`, { waitUntil: 'networkidle0' });
  await page.waitForSelector('.echarts canvas', { timeout: 60000 });
  await shot('home.png', 4500);

  // 4) 个性推荐
  await page.goto(`${BASE}/#/Book/BookRecommend`, { waitUntil: 'networkidle0' });
  await shot('recommend.png');

  // 5) 新书推荐
  await page.goto(`${BASE}/#/Book/BookRecently`, { waitUntil: 'networkidle0' });
  await shot('newbook.png');

  // 6) 图书列表（借阅界面）
  await page.goto(`${BASE}/#/Book/BookBorrow`, { waitUntil: 'networkidle0' });
  await shot('booklist.png');

  // 7) 我的收藏
  await page.goto(`${BASE}/#/User/UserCollection`, { waitUntil: 'networkidle0' });
  await shot('collection.png');

  await browser.close();
  console.log('ALL DONE');
})().catch(async (e) => {
  console.error('ERR', e.message || e);
  try {
    await page.screenshot({ path: '/shots/debug.png' });
    console.log('debug.png saved');
  } catch (_) { /* ignore */ }
  process.exit(1);
});
