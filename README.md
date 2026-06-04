# 🌡️ Smart Temperature Monitor

**A microcontroller-based temperature monitoring system with real-time Android dashboard.** 📡📱

This project demonstrates an **end‑to‑end embedded system** that connects a simulated microcontroller environment to an Android application.

The system reads temperature data from a sensor in **Proteus simulation**, sends it through a **serial communication bridge**, and visualizes the data **live on an Android dashboard**.

It also allows the user to **change the temperature threshold directly from the mobile app**.

---

# 🏗️ Architecture & Tech Stack

### Embedded / Simulation
- **Microcontroller Code:** CodeVision AVR (C)
- **Simulation Environment:** Proteus
- **Serial Interface:** COMPIM (RX/TX communication)

### Communication Layer
- **Bridge Language:** Python
- **Purpose:** Transfers data between Proteus serial port and Android device
- **Virtual Serial Ports:** VPSE (COM1 ↔ COM2)

### Android Application
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Real‑Time Data Visualization:** Live temperature chart

---

# 🔗 System Architecture

The system works as a bridge between a simulated embedded system and a mobile dashboard.

### Data Flow

1. The **temperature sensor** inside Proteus generates temperature values.
2. The **AVR microcontroller firmware** reads the sensor value.
3. Data is transmitted through **serial communication (TXD/RXD)**.
4. A **Python bridge script** reads the serial data.
5. The script forwards the data to the **Android application**.
6. The Android app:
   - Displays the **current temperature**
   - Plots a **live temperature chart**
   - Allows the user to **set the maximum temperature threshold**

### Reverse Communication

The Android application can also send commands back:

Android App → Python Bridge → Serial Port → Microcontroller

This allows **remote configuration of the temperature limit**.

---

# 📱 Application Features

- 📊 **Live Temperature Chart**
- 🌡 **Real-time sensor data monitoring**
- ⚙ **Adjustable maximum temperature threshold**
- 📡 **Bidirectional communication with microcontroller**
- 📜 **Temperature history visualization**

---

# 🖼️ Screenshots

### Android Dashboard
![Dashboard](docs/screenshots/app-dashboard.jpg)

### Live Temperature Chart
![History](docs/screenshots/app-history.jpg)

### Threshold Settings
![Settings](docs/screenshots/app-settings.jpg)

### Proteus Simulation
![Proteus](docs/screenshots/proteus-simulation.png)

---

# 📂 Project Structure

```
smart-temperature-monitor
│
├── android-app
│   └── Android application (Kotlin + Jetpack Compose)
│
├── python-bridge
│   └── Python script for serial communication
│
├── proteus
│   └── Proteus simulation files
│
├── codevision
│   └── AVR firmware source code
│
├── docs
│   └── screenshots and documentation
│
└── README.md
```

---

# ⚙️ How It Works

1. Run the **Proteus simulation**.
2. The **AVR firmware** sends temperature values through the serial port.
3. The **Python bridge** reads serial data using virtual COM ports.
4. The bridge forwards the data to the **Android application**.
5. The Android app updates the **dashboard and charts in real time**.

---

# ⚠️ Project Status

This project was created as a **microcontroller course demonstration project**.

The current version includes all core features but may still contain minor bugs.

---

# 📄 License

Released under the **MIT License**.

---

---

# 🌡️ سیستم پایش دما با میکروکنترلر

**یک سیستم مانیتورینگ دما با داشبورد اندروید و نمایش لحظه‌ای داده‌ها.** 📡📱

این پروژه یک نمونه **سیستم امبدد کامل** است که ارتباط بین شبیه‌سازی میکروکنترلر و یک اپلیکیشن اندروید را نشان می‌دهد.

در این سیستم دمای سنسور در **Proteus** خوانده شده و از طریق **ارتباط سریال** به یک اسکریپت **Python** ارسال می‌شود و سپس در **اپلیکیشن اندروید** به صورت زنده نمایش داده می‌شود.

همچنین کاربر می‌تواند **حداکثر دمای مجاز را از داخل اپلیکیشن تغییر دهد**.

---

## 🏗️ تکنولوژی‌ها

### بخش امبدد
- برنامه‌نویسی میکروکنترلر با **CodeVision AVR**
- شبیه‌سازی مدار در **Proteus**
- ارتباط سریال با **COMPIM**

### لایه ارتباطی
- اسکریپت **Python**
- استفاده از **Virtual Serial Ports Emulator (VPSE)**

### اپلیکیشن اندروید
- زبان **Kotlin**
- رابط کاربری **Jetpack Compose**
- معماری **MVVM**
- رسم نمودار دمای زنده

---

## ⚙️ قابلیت‌ها

- نمایش **دمای لحظه‌ای سنسور**
- رسم **نمودار دمای زنده**
- امکان **تعیین آستانه دما از داخل اپلیکیشن**
- ارتباط **دوطرفه بین اپلیکیشن و میکروکنترلر**
- ثبت **تاریخچه دما**

---

## ⚠️ وضعیت پروژه

این پروژه به عنوان **پروژه دمو برای درس میکروکنترلر** توسعه داده شده است.

---

## 📄 لایسنس

این پروژه تحت لایسنس **MIT** منتشر شده است.
 (چیزی که ریپویت را چند برابر قوی‌تر نشان می‌دهد).
