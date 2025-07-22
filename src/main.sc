require: slotfilling/slotFilling.sc
  module = sys.zb-common

theme: /StartAndEnd

    state: Start
        intent: /привет
        q!: $regex</start>
        random: 
            a: Здравствуйте! Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура.
            a: Приветствую вас! Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
        go!: /StartAndEnd/HowCanIHelpYou

    state: HowCanIHelpYou
        random: 
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
        buttons:
            "Прогноз погоды" -> /WeatherForecast/WeatherForecast
            "Оформить заявку" -> /TravelRequest/TravelRequest
        intent: /нет вопросов || toState = "/StartAndEnd/DontHaveQuestions"
        event: noMatch || toState = "/StartAndEnd/HowCanIHelpYou/CatchAll"

        state: CatchAll
            event: noMatch
            script:
                $session.catchAllCounter = ($session.catchAllCounter || 0) + 1;
            
            if: $session.catchAllCounter < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?
                go!: ../..
            else: 
                a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь, совсем скоро научусь отвечать и на него.
                script:
                    $session.catchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse

    state: DontHaveQuestions
        a: Хорошо! Если будут вопросы - обращайтесь!
        go!: /StartAndEnd/Goodbye

    state: SomethingElse
        random: 
            a: Хотите спросить что-то еще?
            a: Могу ли я помочь чем-то еще?
            a: Подскажите, у вас остались еще вопросы?
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast/WeatherForecast
            "Оформить заявку на тур" -> /TravelRequest/TravelRequest
        intent: /sys/aimylogic/ru/agreement || toState = "/StartAndEnd/HowCanIHelpYou"
        intent: /sys/aimylogic/ru/negation || toState = "/StartAndEnd/DontHaveQuestions"
        event: noMatch || toState = "/StartAndEnd/SomethingElse/CatchAll"

        state: CatchAll
            event: noMatch
            script:
                $session.somethingElseCounter = ($session.somethingElseCounter || 0) + 1;
            if: $session.somethingElseCounter < 3
                random: 
                    a: Извините, не совсем понял. Уточните, что вас интересует?
                    a: Не смог разобрать ваш ответ. Выберите один из вариантов:
                buttons:
                    "Погода" -> /WeatherForecast/WeatherForecast
                    "Тур" -> /TravelRequest/TravelRequest
                go!: ../..
            else: 
                a: Простите, так и не смог понять, что вы имели в виду.
                script:
                    $session.somethingElseCounter = 0;
                go!: /StartAndEnd/Goodbye

    state: Goodbye
        intent: /пока
        random: 
            a: Всего доброго!
            a: Всего вам доброго!
            a: Всего доброго, до свидания!
        script:
            $reactions.transition("/")
    
    
theme: /GeneralStates
    
    state: GlobalCatchAll
        event!: noMatch
        script:
            $session.globalCounter = ($session.globalCounter || 0) + 1;
        if: $session.globalCounter < 3
            random: 
                a: Прошу прощения, не совсем вас понял. Попробуйте, пожалуйста, переформулировать ваш вопрос.
                a: Простите, не совсем понял. Что именно вас интересует?
                a: Простите, не получилось вас понять. Переформулируйте, пожалуйста.
                a: Не совсем понял вас. Пожалуйста, попробуйте задать вопрос по-другому.
            go!: /StartAndEnd/HowCanIHelpYou
        else: 
            a: К сожалению, я не смог обработать ваш запрос. Пожалуйста, попробуйте позже.
            script:
                $session.globalCounter = 0;
            go!: /StartAndEnd/SomethingElse

    state: AreYouRobot
        intent!: /ты робот
        random: 
            a: Я Артур — бот-помощник компании Just Tour, всегда готов отвечать на ваши вопросы.
            a: Вы общаетесь с Артуром — чат-ботом, разработанным командой Just Tour, чтобы помогать вам. Всегда рад пообщаться с вами!
        go!: /StartAndEnd/HowCanIHelpYou

    state: WhatCanYouDo
        intent!: /что ты умеешь
        random: 
            a: Умею рассказывать о погоде в городах мира и составлять заявки на подбор подходящего именно вам путешествия.
            a: С удовольствием расскажу вам о ближайших метеопрогнозах для разных городов и помогу составить запрос на подбор тура.
        go!: /StartAndEnd/HowCanIHelpYou

    state: AnyError
        event!: error
        random: 
            a: Извините, произошла техническая ошибка. Специалисты обязательно изучат ее и возьмут в работу. Пожалуйста, напишите в чат позже.
            a: Простите, произошла ошибка в системе. Наши специалисты обязательно ее исправят. Пожалуйста, напишите мне позже.
        buttons:
            "В начало" -> /StartAndEnd/Start
        script:
            $reactions.transition("/")
    
theme: /WeatherForecast
    state: WeatherForecast
        intent!: /Погода
        go!: /WeatherForecast/GetCity

    state: GetCity
        a: Напишите город
        intent: /sys/aimylogic/ru/city || toState = "/WeatherForecast/GetCity/UserCity"
        event: noMatch || toState = "/WeatherForecast/GetCity/CatchAll"
        
        state: UserCity
            intent: /sys/aimylogic/ru/city
            script:
                $session.city = $parseTree.text;
            go!: /WeatherForecast/GetDate

        state: CatchAll
            event: noMatch
            a: Извините, не нашел такого города. Попробуйте еще раз.
            go!: /WeatherForecast/GetCity

    state: GetDate
        a: Напишите дату
        intent: @duckling.date || toState = "/WeatherForecast/GetDate/UserDate"
        event: noMatch || toState = "/WeatherForecast/GetDate/CatchAll"
        
        state: UserDate
            q: @duckling.date
            script:
                var dateStr = $parseTree.value.value;
                var date = new Date(dateStr);
                $session.unixTimestamp = Math.floor(date.getTime() / 1000);
                $session.date = date.toLocaleDateString("ru-RU");
            go!: /WeatherForecast/TellWeather

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите дату в правильном формате.
            go!: /WeatherForecast/GetDate

    state: TellWeather
        script:
            var apiKey = "965fc776c0584a9863e0574ab2d0e374";
            var url = "https://api.openweathermap.org/data/2.5/weather?q=" + $session.city + "&appid=" + apiKey + "&units=metric&lang=ru";
            try {
                var response = $http.get(url);
                if (response && response.data) {
                    var weatherData = response.data;
                    var temp = weatherData.main.temp;
                    var message = "У меня получилось уточнить: на " + $session.date + " в городе " + $session.city + " температура воздуха составит " + temp + " градусов по Цельсию.";
                    $reactions.answer(message);
                } else {
                    $reactions.answer("Не удалось получить данные о погоде. Попробуйте позже.");
                }
            } catch (e) {
                $reactions.answer("Произошла ошибка при запросе погоды. Попробуйте уточнить город.");
            }
        go!: /StartAndEnd/SomethingElse

    state: Error
        event: error
        a: Произошла ошибка при получении данных о погоде.
        go!: /WeatherForecast/WeatherForecast

    state: SomethingElseForWeather
        state: AnotherOne
            intent: /еще|что-то еще|другое/
            a: Хотите узнать прогноз для другой даты или города?
            go!: /WeatherForecast

        state: Agree
            intent: /да|конечно|хочу/
            a: Отлично! Давайте начнем сначала.
            go!: /WeatherForecast

        state: CatchAll
            event: noMatch
            a: Спасибо за использование сервиса прогноза погоды!
            go!: /StartAndEnd/Goodbye
        
        
theme: /TravelRequest
    state: Init
        script:
            // Инициализация всех переменных сессии
            $session.tourRequest = $session.tourRequest || {
                country: null,
                people: null,
                startDate: null,
                duration: null,
                package: null,
                name: null,
                phone: null,
                comment: null,
                price: null,
                retryCounters: {
                    country: 0,
                    people: 0,
                    date: 0,
                    duration: 0,
                    package: 0,
                    name: 0,
                    phone: 0
                }
            };
            $log("TourRequest session initialized: " + JSON.stringify($session.tourRequest));

    state: TravelRequest
        script:
            $reactions.transition("/TravelRequest/Init");
        go!: /TravelRequest/MainQuestion

    state: MainQuestion
        if: $session.tourRequest.country
            go!: /TravelRequest/AskNumberOfPeople
        else:
            random:
                a: Вы уже выбрали страну для путешествия?
                a: Подскажите, в какую страну планируете поездку?
            go!: /TravelRequest/ProcessCountry

    state: ProcessCountry
        state: GetCountry
            intent: /sys/aimylogic/ru/country
            script:
                $session.tourRequest.country = $parseTree._country || $parseTree.text;
                $session.tourRequest.retryCounters.country = 0;
                $log("Country set: " + $session.tourRequest.country);
            a: Отлично! Страна {{$session.tourRequest.country}} записана. Сколько человек поедет?
            go!: /TravelRequest/AskNumberOfPeople

        state: NoCountry
            intent: /нет|не знаю/
            script:
                $session.tourRequest.country = "Не указана";
                $log("Country not specified");
            a: Хорошо, менеджер поможет с выбором страны. Сколько человек поедет?
            go!: /TravelRequest/AskNumberOfPeople

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.country += 1;
                $log("Country retry #" + $session.tourRequest.retryCounters.country);
            
            if: $session.tourRequest.retryCounters.country < 3
                random:
                    a: Пожалуйста, укажите название страны.
                    a: В какую страну планируете путешествие?
                go!: ..
            else:
                script:
                    $session.tourRequest.country = "Не указана";
                    $session.tourRequest.retryCounters.country = 0;
                a: Пропускаем выбор страны. Сколько человек поедет?
                go!: /TravelRequest/AskNumberOfPeople

    state: AskNumberOfPeople
        a: Укажите количество путешественников:
        buttons:
            "1" -> /TravelRequest/ProcessPeople
            "2" -> /TravelRequest/ProcessPeople
            "3" -> /TravelRequest/ProcessPeople
            "4+" -> /TravelRequest/ProcessPeople
            "Пока не знаю" -> /TravelRequest/ProcessPeople

    state: ProcessPeople
        state: GetNumber
            intent: /sys/aimylogic/ru/number
            script:
                $session.tourRequest.people = parseInt($parseTree._number) || 1;
                $session.tourRequest.retryCounters.people = 0;
                $log("People set: " + $session.tourRequest.people);
            a: Записал {{$session.tourRequest.people}} человек. Когда планируете поездку?
            go!: /TravelRequest/AskDate

        state: DontKnow
            intent: /не знаю|пока не знаю/
            script:
                $session.tourRequest.people = 1;
                $log("Default people count set: 1");
            a: Будем считать 1 человека. Когда планируете поездку?
            go!: /TravelRequest/AskDate

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.people += 1;
                $log("People retry #" + $session.tourRequest.retryCounters.people);
            
            if: $session.tourRequest.retryCounters.people < 2
                a: Пожалуйста, укажите количество цифрой.
                go!: ..
            else:
                script:
                    $session.tourRequest.people = 1;
                    $session.tourRequest.retryCounters.people = 0;
                a: Будем считать 1 человека. Когда планируете поездку?
                go!: /TravelRequest/AskDate

    state: AskDate
        a: Когда планируете поездку? (ДД.ММ.ГГГГ)
        buttons:
            "В этом месяце" -> /TravelRequest/ProcessDate
            "В следующем месяце" -> /TravelRequest/ProcessDate
            "Еще не определились" -> /TravelRequest/ProcessDate

    state: ProcessDate
        state: GetDate
            intent: @duckling.date
            script:
                var date = new Date($parseTree.value.value);
                $session.tourRequest.startDate = date.toISOString();
                $session.tourRequest.formattedDate = date.toLocaleDateString("ru-RU");
                $session.tourRequest.retryCounters.date = 0;
                $log("Date set: " + $session.tourRequest.startDate);
            a: Записал дату {{$session.tourRequest.formattedDate}}. На сколько дней?
            go!: /TravelRequest/AskDuration

        state: DontKnow
            intent: /не знаю|не определились/
            script:
                $session.tourRequest.startDate = null;
                $log("Date not specified");
            a: Даты будут согласованы. На сколько дней планируете?
            go!: /TravelRequest/AskDuration

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.date += 1;
                $log("Date retry #" + $session.tourRequest.retryCounters.date);
            
            if: $session.tourRequest.retryCounters.date < 2
                a: Пожалуйста, укажите дату в формате ДД.ММ.ГГГГ.
                go!: ..
            else:
                script:
                    $session.tourRequest.startDate = null;
                    $session.tourRequest.retryCounters.date = 0;
                a: Пропускаем выбор даты. На сколько дней планируете?
                go!: /TravelRequest/AskDuration

    state: AskDuration
        a: Планируемая продолжительность (в днях):
        buttons:
            "3-5 дней" -> /TravelRequest/ProcessDuration
            "1 неделя" -> /TravelRequest/ProcessDuration
            "2 недели" -> /TravelRequest/ProcessDuration
            "Пока не знаю" -> /TravelRequest/ProcessDuration

    state: ProcessDuration
        state: GetDuration
            intent: /sys/aimylogic/ru/number
            script:
                $session.tourRequest.duration = parseInt($parseTree._number) || 7;
                $session.tourRequest.retryCounters.duration = 0;
                $log("Duration set: " + $session.tourRequest.duration);
            a: Записал {{$session.tourRequest.duration}} дней. Выберите пакет:
            go!: /TravelRequest/AskPackage

        state: DontKnow
            intent: /не знаю/
            script:
                $session.tourRequest.duration = 7;
                $log("Default duration set: 7");
            a: Будем считать 7 дней. Выберите пакет:
            go!: /TravelRequest/AskPackage

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.duration += 1;
                $log("Duration retry #" + $session.tourRequest.retryCounters.duration);
            
            if: $session.tourRequest.retryCounters.duration < 2
                a: Пожалуйста, укажите количество дней.
                go!: ..
            else:
                script:
                    $session.tourRequest.duration = 7;
                    $session.tourRequest.retryCounters.duration = 0;
                a: Будем считать 7 дней. Выберите пакет:
                go!: /TravelRequest/AskPackage

    state: AskPackage
        a: Доступные пакеты:
        
            1. Эконом - 7 000 руб/день
            • Отель 3*
            • Трансфер
        
            2. Стандарт - 12 000 руб/день
            • Отель 4*
            • Трансфер
            • Завтраки
        
            3. VIP - 20 000 руб/день
            • Отель 5*
            • Трансфер
            • Питание
            • Экскурсии
        
            Ваш выбор?
        buttons:
            "Эконом" -> /TravelRequest/ProcessPackage
            "Стандарт" -> /TravelRequest/ProcessPackage
            "VIP" -> /TravelRequest/ProcessPackage
            "Подробнее" -> /TravelRequest/PackageDetails

    state: ProcessPackage
        state: GetPackage
            q: (Эконом|Стандарт|VIP)
            script:
                $session.tourRequest.package = $parseTree.text;
                $session.tourRequest.price = calculatePrice();
                $session.tourRequest.retryCounters.package = 0;
                $log("Package set: " + $session.tourRequest.package);
            a: Пакет "{{$session.tourRequest.package}}" выбран. Ваше имя?
            go!: /TravelRequest/AskName

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.package += 1;
                $log("Package retry #" + $session.tourRequest.retryCounters.package);
            
            if: $session.tourRequest.retryCounters.package < 2
                a: Пожалуйста, выберите один из пакетов.
                go!: /TravelRequest/AskPackage
            else:
                script:
                    $session.tourRequest.package = "Не выбран";
                    $session.tourRequest.retryCounters.package = 0;
                a: Пропускаем выбор пакета. Ваше имя?
                go!: /TravelRequest/AskName

    state: PackageDetails
        a: Подробнее о пакетах:
        
            Эконом:
            - Размещение в отеле 3*
            - Трансфер из аэропорта
            
            Стандарт:
            - Отель 4*
            - Трансфер
            - Завтраки
            
            VIP:
            - Отель 5*
            - Трансфер
            - Питание
            - Экскурсии
            - Страховка
            
            Ваш выбор?
        go!: /TravelRequest/AskPackage

    state: AskName
        a: Ваше имя для заявки:
        
        state: GetName
            q: * *
            script:
                $session.tourRequest.name = $request.query.trim();
                $session.tourRequest.retryCounters.name = 0;
                $log("Name set: " + $session.tourRequest.name);
            a: Спасибо, {{$session.tourRequest.name}}! Ваш телефон?
            go!: /TravelRequest/AskPhone

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.name += 1;
                $log("Name retry #" + $session.tourRequest.retryCounters.name);
            
            if: $session.tourRequest.retryCounters.name < 2
                a: Пожалуйста, укажите ваше имя.
                go!: ..
            else:
                script:
                    $session.tourRequest.name = "Не указано";
                    $session.tourRequest.retryCounters.name = 0;
                a: Имя можно будет указать позже. Ваш телефон?
                go!: /TravelRequest/AskPhone

    state: AskPhone
        a: Контактный телефон:
        
        state: GetPhone
            q: $phone
            script:
                $session.tourRequest.phone = $parseTree._phone || $request.query;
                $session.tourRequest.retryCounters.phone = 0;
                $log("Phone set: " + $session.tourRequest.phone);
            a: Телефон {{$session.tourRequest.phone}} записан. Комментарии?
            go!: /TravelRequest/AskComment

        state: CatchAll
            event: noMatch
            script:
                $session.tourRequest.retryCounters.phone += 1;
                $log("Phone retry #" + $session.tourRequest.retryCounters.phone);
            
            if: $session.tourRequest.retryCounters.phone < 3
                a: Пожалуйста, укажите телефон в формате +7XXX XXX-XX-XX.
                go!: ..
            else:
                a: Без телефона мы не сможем связаться. Попробуйте оставить заявку позже.
                script:
                    $reactions.transition("/StartAndEnd/Goodbye");

    state: AskComment
        a: Дополнительные пожелания:
        
        state: GetComment
            q: *
            script:
                $session.tourRequest.comment = $request.query;
                $log("Comment set: " + $session.tourRequest.comment);
            go!: /TravelRequest/Confirm

        state: NoComment
            intent: /нет|без комментариев/
            script:
                $session.tourRequest.comment = null;
                $log("No comment");
            go!: /TravelRequest/Confirm

    state: Confirm
        script:
            var message = "Проверьте заявку:\n\n" +
                "Страна: " + ($session.tourRequest.country || "Не указана") + "\n" +
                "Количество человек: " + ($session.tourRequest.people || 1) + "\n" +
                "Дата: " + ($session.tourRequest.formattedDate || "Не определена") + "\n" +
                "Дней: " + ($session.tourRequest.duration || 7) + "\n" +
                "Пакет: " + ($session.tourRequest.package || "Не выбран") + "\n";
                
            if ($session.tourRequest.price) {
                message += "Примерная стоимость: " + $session.tourRequest.price + " руб.\n";
            }
            
            message += "Имя: " + ($session.tourRequest.name || "Не указано") + "\n" +
                      "Телефон: " + $session.tourRequest.phone + "\n";
                      
            if ($session.tourRequest.comment) {
                message += "Комментарий: " + $session.tourRequest.comment + "\n";
            }
            
            $reactions.answer(message);
            
        a: Все верно?
        buttons:
            "Да, отправить" -> /TravelRequest/Submit
            "Нет, исправить" -> /TravelRequest/Edit
            "Отменить" -> /StartAndEnd/Goodbye

    state: Submit
        script:
            sendConfirmationEmail();
            $log("Request submitted: " + JSON.stringify($session.tourRequest));
        a: Заявка отправлена! Номер: #{{Math.floor(Math.random() * 10000)}}. Менеджер свяжется в течение часа.
        go!: /StartAndEnd/Goodbye

    state: Edit
        a: Что исправить?
        buttons:
            "Страну" -> /TravelRequest/MainQuestion
            "Количество" -> /TravelRequest/AskNumberOfPeople
            "Дату" -> /TravelRequest/AskDate
            "Пакет" -> /TravelRequest/AskPackage
            "Имя" -> /TravelRequest/AskName
            "Телефон" -> /TravelRequest/AskPhone
            "Комментарий" -> /TravelRequest/AskComment

        script:
            function calculatePrice() {
                if (!$session.tourRequest.package || !$session.tourRequest.people || !$session.tourRequest.duration) {
                    $log("Insufficient data for price calculation");
                    return null;
                }
                
                var priceMap = {
                    "Эконом": 7000,
                    "Стандарт": 12000,
                    "VIP": 20000
                };
                
                var total = priceMap[$session.tourRequest.package] * 
                            $session.tourRequest.people * 
                            $session.tourRequest.duration;
                
                $log("Price calculated: " + total + " for " + 
                     $session.tourRequest.package + " package, " + 
                     $session.tourRequest.people + " people, " + 
                     $session.tourRequest.duration + " days");
                
                return total;
            }
            
            function sendConfirmationEmail() {
                var emailBody = "Новая заявка на тур:\n\n" +
                    "Имя: " + ($session.tourRequest.name || "Не указано") + "\n" +
                    "Телефон: " + $session.tourRequest.phone + "\n" +
                    "Страна: " + ($session.tourRequest.country || "Не указана") + "\n" +
                    "Количество человек: " + ($session.tourRequest.people || 1) + "\n" +
                    "Дата: " + ($session.tourRequest.formattedDate || "Не определена") + "\n" +
                    "Продолжительность: " + ($session.tourRequest.duration || 7) + " дней\n" +
                    "Пакет: " + ($session.tourRequest.package || "Не выбран") + "\n" +
                    "Комментарий: " + ($session.tourRequest.comment || "нет") + "\n\n" +
                    "Полные данные:\n" + JSON.stringify($session.tourRequest, null, 2);
                
                $mail.send("bookings@justtour.com", "Новая заявка от " + ($session.tourRequest.name || "клиента"), emailBody);
                $log("Confirmation email sent");
            }
            

            
