<p align="center">
  <img src="./frontend/src/assets/image/common/logo.png" alt="BookRecommendation logo" width="96">
</p>

<h1 align="center">BookRecommendation</h1>

<p align="center">A university library recommendation system covering student users, library books, borrowing behavior, and a Spark / Hive recommendation pipeline.</p>

<p align="center">
  <a href="./README.md">简体中文</a> | <a href="./README.en.md">English</a>
</p>

<p align="center">
  <img alt="Status" src="https://img.shields.io/badge/status-portfolio-7952B3?style=for-the-badge">
  <img alt="Stack" src="https://img.shields.io/badge/stack-Vue%202%20%2B%20Spring%20Boot%20%2B%20Spark-2E7D32?style=for-the-badge">
  <img alt="Screenshot" src="https://img.shields.io/badge/screenshot-frontend%20only-F59E0B?style=for-the-badge">
  <a href="./LICENSE"><img alt="License" src="https://img.shields.io/badge/license-Apache--2.0-blue?style=for-the-badge"></a>
</p>

<p align="center">
  <img src="./docs/assets/screenshots/frontend-overview.png" alt="BookRecommendation frontend login screenshot" width="900">
</p>

BookRecommendation is a bachelor graduation project and engineering recap. The repository contains a Vue frontend, Spring Boot backend, Spark / Hive / Kafka recommendation module, and a Selenium crawler for missing book covers.

The original database is not included because it contained real student identity and library data. The current repository is intended to demonstrate architecture, implementation style, and frontend interaction rather than ship a runnable production dataset.

## Features

- User registration, login, authentication, and student identity binding.
- Book search, book details, collection status, borrowing behavior, and management flows.
- Kafka user behavior logging.
- Spark Streaming / Spark SQL / Hive recommendation computation.
- Personalized recommendations, related books, and new-book recommendation outputs.
- Selenium-based cover completion workflow.

## Tech Stack

| Area | Stack |
| --- | --- |
| Frontend | Vue 2, Vue Router, Vuex, Element UI, Axios, ECharts |
| Backend | Spring Boot, Spring Security, MyBatis-Plus, MySQL, Druid, JWT |
| Big data | Spark, Spark Streaming, Spark SQL, Hive, Hadoop, Kafka |
| Crawler | Python, Selenium, PyMySQL, ChromeDriver |
| Build tools | Maven, npm |

## Local Development

Frontend-only preview:

```powershell
cd D:\code\BookRecommendation\frontend
npm install
npm run serve
```

The full backend and recommendation pipeline require MySQL plus a Linux / Ubuntu big-data environment with Hadoop, Hive, Spark, and Kafka. Those services are not forced for README screenshots in this pass.

## License and Security

This repository uses the Apache License 2.0. See [LICENSE](LICENSE).

Security reporting instructions are in [SECURITY.md](SECURITY.md), and contribution notes are in [CONTRIBUTING.md](CONTRIBUTING.md).
