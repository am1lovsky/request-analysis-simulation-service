# 🚀 Request Analysis Service

This project is my final project (**Abschlussprojekt**) for the IHK Abschlussprüfung as a *Fachinformatiker für Anwendungsentwicklung*.

---

## 📌 Overview

The **Request Analysis Service** is a Spring Boot–based internal debugging tool designed to support the analysis of HTTP communication and the simulation of different failure scenarios.

It helps developers and QA engineers to better understand system behavior and reproduce hard-to-debug issues.

---

## ⚙️ Features

- 🔍 Analysis of HTTP requests
- 💥 Simulation of failure scenarios
    - custom HTTP status codes
    - response delays
    - broken JSON responses
    - large response bodies
- 🐞 Debug mode with extended response metadata
- 🗄️ Persistence of simulation history in MongoDB

---

## 📊 Purpose

All simulation requests are stored in a MongoDB database, allowing:

- tracking of past test scenarios
- reproducibility of issues
- structured analysis of system behavior

---

## 🔐 Requirements

To run the application, a `.env` file with the required MongoDB connection data must be provided.

---

## 👨‍💻 Author

**Anatoliy Milovsky**