// 嵌入演示 mock 数据（响应结构对齐前端各页面：扁平字段 + bookExtend 双形态；
// 仅 VUE_APP_EMBEDDED_DEMO 构建使用）
const pick = (b) => ({
  mCallNo: b.mCallNo,
  mTitle: b.mTitle,
  mAuthor: b.mAuthor,
  mPublisher: b.mPublisher,
  mPubYear: b.mPubYear,
  status: b.status,
})
const BOOKS = [
  { mCallNo: 'TP312.8JA-1', mTitle: 'Java 核心卷 Ⅰ：基础知识', mAuthor: '凯·霍斯特曼', mPublisher: '机械工业出版社', mPubYear: '2022', status: '可借' },
  { mCallNo: 'TP311.56-1', mTitle: 'Spring 实战（第 6 版）', mAuthor: 'Craig Walls', mPublisher: '人民邮电出版社', mPubYear: '2022', status: '可借' },
  { mCallNo: 'TP312.8JA-2', mTitle: '深入理解 Java 虚拟机（第 3 版）', mAuthor: '周志明', mPublisher: '机械工业出版社', mPubYear: '2019', status: '可借' },
  { mCallNo: 'TP311.5-2', mTitle: '代码整洁之道', mAuthor: '罗伯特·马丁', mPublisher: '人民邮电出版社', mPubYear: '2020', status: '在馆' },
  { mCallNo: 'TP311.5-3', mTitle: '重构：改善既有代码的设计（第 2 版）', mAuthor: '马丁·福勒', mPublisher: '人民邮电出版社', mPubYear: '2021', status: '可借' },
  { mCallNo: 'TP312.8JA-3', mTitle: 'Effective Java（第 3 版）', mAuthor: '约书亚·布洛克', mPublisher: '机械工业出版社', mPubYear: '2019', status: '借出' },
  { mCallNo: 'TP311.13-1', mTitle: 'SQL 必知必会（第 5 版）', mAuthor: '本·福达', mPublisher: '人民邮电出版社', mPubYear: '2020', status: '可借' },
  { mCallNo: 'TP391.4-1', mTitle: '大话数据结构', mAuthor: '程杰', mPublisher: '清华大学出版社', mPubYear: '2016', status: '在馆' },
  { mCallNo: 'TP316.8-1', mTitle: 'Hadoop 权威指南（第 4 版）', mAuthor: 'Tom White', mPublisher: '清华大学出版社', mPubYear: '2016', status: '可借' },
  { mCallNo: 'TP316.8-2', mTitle: 'Kafka 权威指南', mAuthor: 'Neha Narkhede', mPublisher: '人民邮电出版社', mPubYear: '2018', status: '可借' },
  { mCallNo: 'TP393.09-1', mTitle: '深入浅出 Node.js', mAuthor: '朴灵', mPublisher: '人民邮电出版社', mPubYear: '2013', status: '在馆' },
  { mCallNo: 'TP312.8JS-1', mTitle: 'JavaScript 高级程序设计（第 4 版）', mAuthor: '马特·弗里斯比', mPublisher: '人民邮电出版社', mPubYear: '2020', status: '可借' },
  { mCallNo: 'TP312.8JS-2', mTitle: '深入浅出 Vue.js', mAuthor: '刘博文', mPublisher: '人民邮电出版社', mPubYear: '2019', status: '可借' },
  { mCallNo: 'TP311.1-1', mTitle: '算法导论（原书第 3 版）', mAuthor: '托马斯·科尔曼', mPublisher: '机械工业出版社', mPubYear: '2013', status: '借出' },
  { mCallNo: 'TP311.5-4', mTitle: '人月神话（40 周年纪念版）', mAuthor: '弗雷德里克·布鲁克斯', mPublisher: '清华大学出版社', mPubYear: '2015', status: '在馆' },
  { mCallNo: 'TP181-1', mTitle: '机器学习（西瓜书）', mAuthor: '周志华', mPublisher: '清华大学出版社', mPubYear: '2016', status: '可借' },
]
const bookExtend = (b) => ({ ...pick(b), img: 'not found', callNo: b.mCallNo, author: b.mAuthor, pubYear: b.mPubYear, readNum: 0 })
const row = (b) => ({ ...pick(b), bookExtend: bookExtend(b) })
const books = BOOKS.map(row)
const recommend = books.slice(0, 8)
const newbooks = books.slice(4, 12)
const related = books.slice(1, 5).map((b) => ({
  ...pick(b),
  bookExtend: { ...bookExtend(b), mTitle: b.mTitle },
}))
const lends = [
  { callId: 1, certId: '2020001', name: '演示用户', mCallNo: 'TP311.56-1', mTitle: 'Spring 实战（第 6 版）', mPubYear: '2022', status: '借阅中', lendDate: '2026-8-26', endDate: '2026-9-26' },
  { callId: 2, certId: '2020001', name: '演示用户', mCallNo: 'TP312.8JA-3', mTitle: 'Effective Java（第 3 版）', mPubYear: '2019', status: '借阅中', lendDate: '2026-8-29', endDate: '2026-9-29' },
]
const ok = (data) => ({ code: 200, msg: '成功', data })

// URL 前缀 → mock 数据（写操作统一返回成功）
export default {
  '/login': () => ({ msg: '认证通过', code: 200 }),
  '/sys/user/selectByAccount': () => ok({ account: '2020001', certId: '2020001', head: '1004522750_head.jpg', password: '123456', status: 0, time: '2026-8-9', username: '演示用户' }),
  '/sys/user/selectByCertId': () => ok(null),
  '/library/recommend/getRecommend': () => ok({ list: recommend, totalCount: recommend.length }),
  '/library/newBook/queryNewBook': () => ok({ list: newbooks, totalCount: newbooks.length }),
  '/library/book/queryBook': () => ok({ list: books, totalCount: books.length }),
  '/library/book/selectByCallNo': () => ok(books[1]),
  '/library/book/selectBookExtendByCallNo': () => ok(books[1]),
  '/library/relatedBook/getRelatedBook': () => ok(related),
  '/library/collect/queryByCertId': () => ok([
    { certId: '2020001', name: '演示用户', mCallNo: 'TP312.8JA-1', mTitle: 'Java 核心卷 Ⅰ：基础知识', mAuthor: '凯·霍斯特曼', mPublisher: '机械工业出版社', mPubYear: '2022', status: 'False' },
    { certId: '2020001', name: '演示用户', mCallNo: 'TP311.56-1', mTitle: 'Spring 实战（第 6 版）', mAuthor: 'Craig Walls', mPublisher: '人民邮电出版社', mPubYear: '2022', status: 'False' },
  ]),
  '/library/lend/queryLends': () => ok(lends),
  '/library/lend/list': () => ok({ list: lends, totalCount: lends.length }),
  '/library/lend/save': () => ok(null),
  '/library/lend/delete': () => ok(null),
  '/library/collect/queryCollect': () => ok([]),
  '/library/collect/saveCollect': () => ok(null),
  '/library/collect/deleteCollect': () => ok(null),
  '/library/book/save': () => ok(null),
  '/sys/user/list': () => ok([]),
  '/sys/user/update': () => ok(null),
  '/sys/user/updateUserName': () => ok(null),
  '/sys/user/save': () => ok(null),
  '/sys/user/upload': () => ok(null),
  '/library/reader/checkCertId': () => ok(null),
}
