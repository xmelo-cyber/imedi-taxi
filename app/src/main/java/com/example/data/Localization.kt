package com.example.data

object Localization {
    fun get(key: String, lang: AppLanguage): String {
        return strings[key]?.get(lang) ?: strings[key]?.get(AppLanguage.KA) ?: key
    }

    private val strings = mapOf(
        "app_title" to mapOf(
            AppLanguage.KA to "Taxigo",
            AppLanguage.EN to "Taxigo",
            AppLanguage.RU to "Taxigo"
        ),
        "login" to mapOf(
            AppLanguage.KA to "შესვლა",
            AppLanguage.EN to "Log In",
            AppLanguage.RU to "Войти"
        ),
        "register" to mapOf(
            AppLanguage.KA to "რეგისტრაცია",
            AppLanguage.EN to "Register",
            AppLanguage.RU to "Регистрация"
        ),
        "forgot_password" to mapOf(
            AppLanguage.KA to "პაროლის აღდგენა",
            AppLanguage.EN to "Forgot Password",
            AppLanguage.RU to "Забыли пароль?"
        ),
        "phone_or_email" to mapOf(
            AppLanguage.KA to "ტელეფონის ნომერი ან ელ-ფოსტა",
            AppLanguage.EN to "Phone number or email",
            AppLanguage.RU to "Номер телефона или эл. почта"
        ),
        "password" to mapOf(
            AppLanguage.KA to "პაროლი",
            AppLanguage.EN to "Password",
            AppLanguage.RU to "Пароль"
        ),
        "biometric_login" to mapOf(
            AppLanguage.KA to "ბიომეტრიით შესვლა (Face / Fingerprint)",
            AppLanguage.EN to "Biometric Login (Face / Touch ID)",
            AppLanguage.RU to "Вход по биометрии"
        ),
        "role_passenger" to mapOf(
            AppLanguage.KA to "მე ვარ მგზავრი",
            AppLanguage.EN to "I am a Passenger",
            AppLanguage.RU to "Я пассажир"
        ),
        "role_driver" to mapOf(
            AppLanguage.KA to "მე ვარ მძღოლი",
            AppLanguage.EN to "I am a Driver",
            AppLanguage.RU to "Я водитель"
        ),
        "first_name" to mapOf(
            AppLanguage.KA to "სახელი",
            AppLanguage.EN to "First Name",
            AppLanguage.RU to "Имя"
        ),
        "last_name" to mapOf(
            AppLanguage.KA to "გვარი",
            AppLanguage.EN to "Last Name",
            AppLanguage.RU to "Фамилия"
        ),
        "car_make_model" to mapOf(
            AppLanguage.KA to "მანქანის მარკა და მოდელი (მაგ: Toyota Prius)",
            AppLanguage.EN to "Car Make & Model (e.g. Toyota Prius)",
            AppLanguage.RU to "Марка и модель авто (напр. Toyota Prius)"
        ),
        "plate_number" to mapOf(
            AppLanguage.KA to "სახელმწიფო ნომერი (მაგ: TX-789-GO)",
            AppLanguage.EN to "License Plate Number",
            AppLanguage.RU to "Гос. номер автомобиля"
        ),
        "car_color_year" to mapOf(
            AppLanguage.KA to "ფერი და გამოშვების წელი",
            AppLanguage.EN to "Color and Manufacturing Year",
            AppLanguage.RU to "Цвет и год выпуска"
        ),
        "driver_license" to mapOf(
            AppLanguage.KA to "მართვის მოწმობის ნომერი",
            AppLanguage.EN to "Driver's License ID",
            AppLanguage.RU to "Номер водительского удостоверения"
        ),
        "tech_passport" to mapOf(
            AppLanguage.KA to "ტექ-პასპორტის ნომერი",
            AppLanguage.EN to "Vehicle Registration / Tech Passport",
            AppLanguage.RU to "Номер техпаспорта"
        ),
        "otp_title" to mapOf(
            AppLanguage.KA to "SMS კოდის დადასტურება",
            AppLanguage.EN to "SMS OTP Verification",
            AppLanguage.RU to "Подтверждение по SMS"
        ),
        "otp_sent" to mapOf(
            AppLanguage.KA to "ერთჯერადი კოდი გამოგზავნილია თქვენს ნომერზე: 4892",
            AppLanguage.EN to "One-time code sent to your number: 4892",
            AppLanguage.RU to "Код отправлен на ваш номер: 4892"
        ),
        "verify_continue" to mapOf(
            AppLanguage.KA to "დადასტურება და გაგრძელება",
            AppLanguage.EN to "Verify & Continue",
            AppLanguage.RU to "Подтвердить и продолжить"
        ),
        "where_to" to mapOf(
            AppLanguage.KA to "სად მივდივართ?",
            AppLanguage.EN to "Where to?",
            AppLanguage.RU to "Куда едем?"
        ),
        "pickup_point" to mapOf(
            AppLanguage.KA to "საიდან (აყვანის წერტილი)",
            AppLanguage.EN to "Pickup location",
            AppLanguage.RU to "Откуда"
        ),
        "destination_point" to mapOf(
            AppLanguage.KA to "სად (დანიშნულების ადგილი)",
            AppLanguage.EN to "Destination location",
            AppLanguage.RU to "Куда"
        ),
        "order_taxi" to mapOf(
            AppLanguage.KA to "ტაქსის გამოძახება",
            AppLanguage.EN to "Order Taxigo",
            AppLanguage.RU to "Заказать такси"
        ),
        "searching_driver" to mapOf(
            AppLanguage.KA to "უახლოესი მძღოლის ძებნა...",
            AppLanguage.EN to "Searching for nearest driver...",
            AppLanguage.RU to "Поиск ближайшего водителя..."
        ),
        "cancel_order" to mapOf(
            AppLanguage.KA to "შეკვეთის გაუქმება",
            AppLanguage.EN to "Cancel Order",
            AppLanguage.RU to "Отменить заказ"
        ),
        "driver_assigned" to mapOf(
            AppLanguage.KA to "მძღოლი მოდის თქვენკენ",
            AppLanguage.EN to "Driver is on the way",
            AppLanguage.RU to "Водитель едет к вам"
        ),
        "driver_arrived" to mapOf(
            AppLanguage.KA to "მძღოლი მოვიდა და გელოდებათ",
            AppLanguage.EN to "Driver has arrived and is waiting",
            AppLanguage.RU to "Водитель прибыл и ждет"
        ),
        "trip_started" to mapOf(
            AppLanguage.KA to "მგზავრობა მიმდინარეობს",
            AppLanguage.EN to "Trip in progress",
            AppLanguage.RU to "Поездка началась"
        ),
        "trip_completed" to mapOf(
            AppLanguage.KA to "მგზავრობა დასრულდა!",
            AppLanguage.EN to "Trip Completed!",
            AppLanguage.RU to "Поездка завершена!"
        ),
        "call_driver" to mapOf(
            AppLanguage.KA to "ზარი (ანონიმური)",
            AppLanguage.EN to "Call (Masked)",
            AppLanguage.RU to "Звонок (анонимный)"
        ),
        "chat_driver" to mapOf(
            AppLanguage.KA to "ჩატი",
            AppLanguage.EN to "Chat",
            AppLanguage.RU to "Чат"
        ),
        "sos_button" to mapOf(
            AppLanguage.KA to "SOS / უსაფრთხოება",
            AppLanguage.EN to "SOS Emergency",
            AppLanguage.RU to "SOS безопасность"
        ),
        "share_ride" to mapOf(
            AppLanguage.KA to "მარშრუტის გაზიარება",
            AppLanguage.EN to "Share Live Ride",
            AppLanguage.RU to "Поделиться поездкой"
        ),
        "promo_code" to mapOf(
            AppLanguage.KA to "პრომოკოდი",
            AppLanguage.EN to "Promo Code",
            AppLanguage.RU to "Промокод"
        ),
        "apply" to mapOf(
            AppLanguage.KA to "გააქტიურება",
            AppLanguage.EN to "Apply",
            AppLanguage.RU to "Применить"
        ),
        "payment_method" to mapOf(
            AppLanguage.KA to "გადახდის მეთოდი",
            AppLanguage.EN to "Payment Method",
            AppLanguage.RU to "Способ оплаты"
        ),
        "cash" to mapOf(
            AppLanguage.KA to "ნაღდი ფული",
            AppLanguage.EN to "Cash",
            AppLanguage.RU to "Наличные"
        ),
        "tip_driver" to mapOf(
            AppLanguage.KA to "ჩაი მძღოლისთვის",
            AppLanguage.EN to "Tip Driver",
            AppLanguage.RU to "Чаевые водителю"
        ),
        "rate_ride" to mapOf(
            AppLanguage.KA to "შეაფასეთ მგზავრობა",
            AppLanguage.EN to "Rate your experience",
            AppLanguage.RU to "Оцените поездку"
        ),
        "done" to mapOf(
            AppLanguage.KA to "დასრულება",
            AppLanguage.EN to "Done",
            AppLanguage.RU to "Готово"
        ),
        "online" to mapOf(
            AppLanguage.KA to "ონლაინ",
            AppLanguage.EN to "Online",
            AppLanguage.RU to "В сети"
        ),
        "offline" to mapOf(
            AppLanguage.KA to "ოფლაინ",
            AppLanguage.EN to "Offline",
            AppLanguage.RU to "Не в сети"
        ),
        "incoming_order" to mapOf(
            AppLanguage.KA to "შემომავალი შეკვეთა!",
            AppLanguage.EN to "Incoming Ride Request!",
            AppLanguage.RU to "Новый заказ!"
        ),
        "accept" to mapOf(
            AppLanguage.KA to "მიღება",
            AppLanguage.EN to "Accept",
            AppLanguage.RU to "Принять"
        ),
        "decline" to mapOf(
            AppLanguage.KA to "უარყოფა",
            AppLanguage.EN to "Decline",
            AppLanguage.RU to "Отклонить"
        ),
        "on_the_way" to mapOf(
            AppLanguage.KA to "გზაში ვარ",
            AppLanguage.EN to "On The Way",
            AppLanguage.RU to "В пути"
        ),
        "arrived_btn" to mapOf(
            AppLanguage.KA to "მივედი",
            AppLanguage.EN to "Arrived",
            AppLanguage.RU to "Прибыл"
        ),
        "start_trip_btn" to mapOf(
            AppLanguage.KA to "დაწყება",
            AppLanguage.EN to "Start Trip",
            AppLanguage.RU to "Начать поездку"
        ),
        "complete_trip_btn" to mapOf(
            AppLanguage.KA to "დასრულება",
            AppLanguage.EN to "Complete Trip",
            AppLanguage.RU to "Завершить поездку"
        ),
        "earnings" to mapOf(
            AppLanguage.KA to "შემოსავალი",
            AppLanguage.EN to "Earnings",
            AppLanguage.RU to "Доходы"
        ),
        "documents" to mapOf(
            AppLanguage.KA to "დოკუმენტები",
            AppLanguage.EN to "Documents",
            AppLanguage.RU to "Документы"
        ),
        "admin_panel" to mapOf(
            AppLanguage.KA to "ადმინ პანელი",
            AppLanguage.EN to "Admin Panel",
            AppLanguage.RU to "Панель администратора"
        ),
        "switch_to_driver" to mapOf(
            AppLanguage.KA to "მძღოლის რეჟიმზე გადასვლა",
            AppLanguage.EN to "Switch to Driver Mode",
            AppLanguage.RU to "Переключиться на водителя"
        ),
        "switch_to_passenger" to mapOf(
            AppLanguage.KA to "მგზავრის რეჟიმზე გადასვლა",
            AppLanguage.EN to "Switch to Passenger Mode",
            AppLanguage.RU to "Переключиться на пассажира"
        ),
        "saved_addresses" to mapOf(
            AppLanguage.KA to "შენახული მისამართები",
            AppLanguage.EN to "Saved Places",
            AppLanguage.RU to "Сохраненные адреса"
        ),
        "home" to mapOf(
            AppLanguage.KA to "სახლი",
            AppLanguage.EN to "Home",
            AppLanguage.RU to "Дом"
        ),
        "work" to mapOf(
            AppLanguage.KA to "სამსახური",
            AppLanguage.EN to "Work",
            AppLanguage.RU to "Работа"
        ),
        "schedule_ride" to mapOf(
            AppLanguage.KA to "წინასწარი შეკვეთა (დაგეგმვა)",
            AppLanguage.EN to "Schedule Ride",
            AppLanguage.RU to "Запланировать поездку"
        ),
        "add_stop" to mapOf(
            AppLanguage.KA to "+ გაჩერება",
            AppLanguage.EN to "+ Add Stop",
            AppLanguage.RU to "+ Добавить остановку"
        )
    )
}
