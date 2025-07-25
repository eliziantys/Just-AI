require: slotfilling/slotFilling.sc
  module = sys.zb-common

theme: /StartAndEnd

    state: Start
        intent: /привет
        random: 
            a: Здравствуйте!
                 Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура.
            a: Приветствую вас! 
                Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
        go!: /StartAndEnd/HowCanIHelpYou
    
    
    state: Start1
        q!: $regex</start>
        random: 
            a: Здравствуйте!
                 Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура.
            a: Приветствую вас! 
                Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
            script:
                $session.tourData = {
                    country: null,
                    people: null,
                    startDate: null,
                    duration: null,
                    endDate: null,
                    package: null,
                    name: null,
                    phone: null,
                    comment: null,
                    price: null
                };
                log("Инициализирована новая структура tourData");    
        go!: /StartAndEnd/HowCanIHelpYou
        
    state: HowCanIHelpYou
        random: 
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
        buttons:
            "Прогноз погоды" -> /WeatherForecast/WeatherForecast
            "Оформить заявку" -> /TravelRequest/Init
        intent: /нет вопросов || toState = "/StartAndEnd/DontHaveQuestions"
        event: noMatch || toState = "/StartAndEnd/HowCanIHelpYou/CatchAll"

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.catchAllCounter = ($session.catchAllCounter || 0) + 1;
            if: $session.catchAllCounter < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?
            else: 
                a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь, совсем скоро научусь отвечать и на него.
                script:
                    $session.catchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse

    state: DontHaveQuestions
        intent!: /нет вопросов
        random: 
            a: Вас понял!
                
            a: Хорошо!
            a: Понял!
        go!: /StartAndEnd/Goodbye

    state: SomethingElse
        random: 
            a: Хотите спросить что-то еще?
            a: Могу ли я помочь чем-то еще?
            a: Подскажите, у вас остались еще вопросы?
        buttons:
            "Прогноз погоды" -> /WeatherForecast/WeatherForecast
            "Оформить заявку" -> /TravelRequest/Init
        intent: /sys/aimylogic/ru/agreement || toState = "/StartAndEnd/HowCanIHelpYou"
        intent: /sys/aimylogic/ru/negation || toState = "/StartAndEnd/DontHaveQuestions"
        intent: /Погода || toState = "/WeatherForecast/WeatherForecast"
        intent: /Оформить заявку || toState = "/TravelRequest/Init"
        event: noMatch || toState = "/StartAndEnd/SomethingElse/CatchAll"

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.somethingElseCounter = ($session.somethingElseCounter || 0) + 1;
            if: $session.somethingElseCounter < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я еще чем-то помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, остались ли у вас еще вопросы?
                buttons:
                    "Погода" -> /WeatherForecast/WeatherForecast
                    "Тур" -> /TravelRequest/Init
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
            script:
                $session.tourData = {
                    country: null,
                    people: null,
                    startDate: null,
                    duration: null,
                    endDate: null,
                    package: null,
                    name: null,
                    phone: null,
                    comment: null,
                    price: null
                };
                log("Инициализирована новая структура tourData");    
            $reactions.transition("/")
    
    
theme: /GeneralStates
    
    state: GlobalCatchAll || noContext = true
        event!: noMatch
        script:
            $session.globalCounter = ($session.globalCounter || 0) + 1;
        if: $session.globalCounter < 3
            random: 
                a: Прошу прощения, не совсем вас понял. Попробуйте, пожалуйста, переформулировать ваш вопрос.
                a: Простите, не совсем понял. Что именно вас интересует?
                a: Простите, не получилось вас понять. Переформулируйте, пожалуйста.
                a: Не совсем понял вас. Пожалуйста, попробуйте задать вопрос по-другому.
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

theme: /WeatherForecast
    state: WeatherForecast
        intent!: /Погода
        go!: /WeatherForecast/GetCity

    state: GetCity
        random:
            a: Укажите, пожалуйста, название города, для которого хотите узнать прогноз погоды.
            a: Скажите, пожалуйста, для какого города вы хотите получить прогноз?
            a: Прогноз для какого города хотите получить?
        intent: /sys/aimylogic/ru/city || toState = "/WeatherForecast/GetCity/UserCity"
        event: noMatch || toState = "/WeatherForecast/GetCity/CatchAll"
        
        state: UserCity
            intent: /sys/aimylogic/ru/city
            script:
                $session.city = $parseTree.text;
            go!: /WeatherForecast/GetDate

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.cityCatchAllCounter = ($session.cityCatchAllCounter || 0) + 1;
            
            if: $session.cityCatchAllCounter < 3
                random: 
                    a: Извините, не совсем понял вас. Напишите, пожалуйста, название города, чтобы я смог узнать прогноз погоды для него.
                    a: К сожалению, не понял вас. Укажите, пожалуйста, нужный вам город.
            else: 
                a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                script:
                    $session.cityCatchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse

    state: GetDate
        random:
            a: На какую дату требуется прогноз?
            a: Прогноз погоды на какую дату вам нужен?
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

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.dateCatchAllCounter = ($session.dateCatchAllCounter || 0) + 1;
            
            if: $session.dateCatchAllCounter < 3
                random:
                    a: Извините, не совсем понял вас. Напишите, пожалуйста, нужную вам дату.
                    a: К сожалению, не понял вас. Введите, пожалуйста, дату, которая вам нужна.
            else: 
                a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                script:
                    $session.dateCatchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse

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
            $reactions.answer("Мне очень жаль, но при обращении к сервису, содержащему сведения о погоде, произошла ошибка. Пожалуйста, попробуйте написать мне немного позже. Надеюсь, работоспособность сервиса восстановится.");
                }
        go!: /WeatherForecast/OfferTourRussia

    state: Error
        event: error
        a: Мне очень жаль, но при обращении к сервису, содержащему сведения о погоде, произошла ошибка. Пожалуйста, попробуйте написать мне немного позже. Надеюсь, работоспособность сервиса восстановится.
        go!: /WeatherForecast/SomethingElseForWeather

    state: SomethingElseForWeather
        random: 
            a: Хотите спросить что-то еще?
            a: Могу ли я помочь чем-то еще?
            a: Подсrкажите, у вас остались еще вопросы?
        buttons:
            "Погода" -> /WeatherForecast/WeatherForecast
            "Тур" -> /TravelRequest/Init
        intent: /sys/aimylogic/ru/agreement || toState = "/WeatherForecast/SomethingElseForWeather/Agree"
        intent: /sys/aimylogic/ru/negation || toState = "/StartAndEnd/DontHaveQuestions"
        event: noMatch || toState = "/WeatherForecast/SomethingElseForWeather/CatchAll"

        state: AnotherOne
            intent: /еще|что-то еще|другое/
            go!: /WeatherForecast

        state: Agree
            intent: /да|конечно|хочу/
            a: Отлично! Давайте начнем сначала.
            go!: /WeatherForecast

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.dateCatchAllCounter = ($session.dateCatchAllCounter || 0) + 1;
            
            if: $session.dateCatchAllCounter < 3
                random:
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я еще чем-то помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, остались ли у вас еще вопросы?
                buttons:
                    "Погода" -> /WeatherForecast/WeatherForecast
                    "Тур" -> /TravelRequest/Init
            else: 
                a: Простите, так и не смог понять, что вы имели в виду..
                script:
                    $session.dateCatchAllCounter = 0;
                go!: /StartAndEnd/Goodbye

    state: OfferTourRussia
        random: 
            a: Хотите оставить заявку на подбор тура в Россию?
            a: Можем составить заявку на подбор идеального тура в Россию. Хотите?
        intent: /sys/aimylogic/ru/agreement || toState = "/TravelRequest/InitFrom"
        intent: /sys/aimylogic/ru/negation || toState = "/WeatherForecast/OfferTourRussia/Disagree"
        event: noMatch || toState = "/WeatherForecast/OfferTourRussia/CatchAll"

        state: Disagree
            a: Понял вас!
            a: В таком случае, желаете узнать погоду в другом городе мира?
            intent: /sys/aimylogic/ru/negation || toState = "/StartAndEnd/SomethingElse"
            intent: /sys/aimylogic/ru/agreement || toState = "/WeatherForecast/WeatherForecast"
            event: noMatch || toState = "/WeatherForecast/OfferTourRussia/Disagree/CatchAll"

            state: CatchAll || noContext = true
             event: noMatch
            script:
                $session.dateCatchAllCounter = ($session.dateCatchAllCounter || 0) + 1;
            
            if: $session.dateCatchAllCounter < 3
                a: Простите, не совсем понял. Хотите узнать прогноз погоды для другого города?
            else: 
                a: Простите, так и не смог понять, что вы имели в виду..
                script:
                    $session.dateCatchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.dateCatchAllCounter = ($session.dateCatchAllCounter || 0) + 1;
            
            if: $session.dateCatchAllCounter < 3
                a: Извините, не совсем понял вас. вы желаете оставить запрос на подбор путевки в Россию?
            else: 
                a: Простите, так и не смог понять, что вы имели в виду..
                script:
                    $session.dateCatchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse

theme: /TravelRequest
    state: InitFrom
            script:
                if (!$session.tourData) {
                    $session.tourData = {
                        country: "Россия",  // Автоматически устанавливаем страну
                        people: null,
                        startDate: null,
                        duration: null,
                        endDate: null,
                        package: null,
                        name: null,
                        phone: null,
                        comment: null,
                        price: null
                    };
                    log("Инициализирована новая структура tourData для России");
                } else {
                    // Если данные уже есть, но страна не Россия - перезаписываем
                    $session.tourData.country = "Россия";
                }
                log("Текущие данные тура: " + JSON.stringify($session.tourData));
        
            go!: /TravelRequest/AskNumberOfPeople
    state: Init
        intent!: /Оформить заявку
        script:
            if (!$session.tourData) {
                $session.tourData = {
            country: null,
            people: null,
            startDate: null,
            duration: null,
            endDate: null,
            package: null,
            name: null,
            phone: null,
            comment: null,
            price: null
                };
                log("Инициализирована новая структура tourData");
            }
            log("Текущие данные тура: " + JSON.stringify($session.tourData));
        go!: /TravelRequest/Travel
            

    state: Travel
        random:
            a: Готов помочь вам оформить заявку на подбор тура. Как только я соберу от вас нужные для запроса данные, наш менеджер подберет самые подходящие варианты и свяжется с вами.
            a: Рад помочь с оформлением запроса на подбор тура. Как только мы заполним заявку, наш специалист свяжется с вами, чтобы предложить наиболее подходящие варианты путешествий.
        a: Подскажите, вы уже определились с страной прибытия?
        intent: /sys/aimylogic/ru/country || toState = "/TravelRequest/Travel/ProcessCountry"
        intent: /sys/aimylogic/ru/agreement || toState = "/TravelRequest/Travel/AskCountry"
        intent: /sys/aimylogic/ru/negation || toState = "/TravelRequest/Travel/Disagree"
        event: noMatch || toState = "/TravelRequest/Travel/CatchAll"

        state: ProcessCountry
            intent: /sys/aimylogic/ru/country
            script:
                $session.tourData.country = $parseTree._country || $parseTree.text;
                log("Страна сохранена: " + $session.tourData.country);
            a: Отлично, я передам консультанту, что местом пребывания станет {{$session.tourData.country}}. А теперь давайте перейдем к указанию оставшихся параметров.
            go!: /TravelRequest/AskNumberOfPeople
            
        state: AskCountry
            intent: /sys/aimylogic/ru/agreement
            a: Введите название страны.
            intent: /sys/aimylogic/ru/country || toState = "/TravelRequest/Travel/ProcessCountry"
            event: noMatch || toState = "/TravelRequest/Travel/CatchAll"

        state: Disagree
            intent: /sys/aimylogic/ru/negation
            a: Понял вас. В таком случае, когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
            script:
                $session.tourData.country = "Не указана";
            go!: /TravelRequest/AskNumberOfPeople

        state: CatchAll || noContext = true
            script:
                $session.countryCatchAllCounter = ($session.countryCatchAllCounter || 0) + 1;
            if: $session.countryCatchAllCounter < 3
                random: 
                    a: Извините, не совсем понял вас. Подскажите, вы выбрали страну для путешествия?
                    a: К сожалению, не понял вас. Вы выбрали страну для поездки?
            else: 
                a: Простите! Так и не получилось вас понять. Когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
                script:
                    $session.countryCatchAllCounter = 0;
                    $session.tourData.country = "Не указана";
                go!: /TravelRequest/AskNumberOfPeople

    state: AskNumberOfPeople
        a: Укажите количество человек, которые отправятся в путешествие.
        intent: /sys/aimylogic/ru/uncertainty || toState = "/TravelRequest/AskNumberOfPeople/DontKnow"
        event: noMatch || toState = "/TravelRequest/AskNumberOfPeople/CatchAll"

        state: CatchNumber
            q: * (@duckling.number) *
            q: * (@caila.entities.NUMBER) *
            script:
                var peopleCount = parseFloat($parseTree._number || $parseTree.value);
                
                // Кросс-совместимая проверка на целое число
                function isInteger(num) {
                    return typeof num === 'number' && 
                           isFinite(num) && 
                           Math.floor(num) === num;
                }
                
                if (isNaN(peopleCount) || peopleCount <= 0 || !isInteger(peopleCount)) {
                    $reactions.answer("К сожалению, не могу принять такой ответ. Пожалуйста, введите валидное число людей - оно должно быть больше 0.");
                    $reactions.transition("/TravelRequest/AskNumberOfPeople");
                } else {
                    $session.tourData.people = peopleCount;
                    var pluralForm = getPeoplePluralForm(peopleCount);
                    $reactions.answer("Записал " + peopleCount + " " + pluralForm + ".");
                    $reactions.transition("/TravelRequest/AskStartDate");
                }
                
                function getPeoplePluralForm(number) {
                    var lastTwo = number % 100;
                    var lastOne = number % 10;
                    if (lastTwo >= 11 && lastTwo <= 14) return "человек";
                    if (lastOne === 1) return "человек";
                    if (lastOne >= 2 && lastOne <= 4) return "человека";
                    return "человек";
                }
        
        state: DontKnow
            intent: /sys/aimylogic/ru/uncertainty
            a: Хорошо, вы можете указать количество позже.
            script:
                $session.tourData.people = "Не указано";
                log("Дата начала не указана");
            go!: /TravelRequest/AskStartDate
         
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.peopleCatchAllCounter = ($session.peopleCatchAllCounter || 0) + 1;
            
            if: $session.peopleCatchAllCounter < 3
                random:
                    a: Извините, не совсем понял вас. Сколько человек планирует отправиться в поездку?
                    a: К сожалению, не понял вас. Сколько человек поедет в тур?
            else: 
                script:
                    $session.peopleCatchAllCounter = 0;
                go!: /TravelRequest/AskNumberOfPeople/DontKnow

    state: AskStartDate
        a: Еще мне потребуется предполагаемая дата начала поездки. Пожалуйста, напишите ее.
        intent: /sys/aimylogic/ru/uncertainty || toState = "/TravelRequest/AskStartDate/DontKnow"
        event: noMatch || toState = "/TravelRequest/AskStartDate/CatchAll"
        q: @ducling.number || toState = "/TravelRequest/AskStartDate/Date"

        state: Date
            q: @duckling.date
            script:
                try {
                    var ducklingArray = $parseTree["duckling.date"];
                    if (!ducklingArray || ducklingArray.length === 0) {
                        throw new Error("duckling.date отсутствует");
                }
            
                    var dateStr = ducklingArray[0].value.value;
                    var date = new Date(dateStr);
                    if (isNaN(date.getTime())) {
                        throw new Error("Невалидная дата: " + dateStr);
                    }
            
                    $session.tourData.startDate = date.toISOString();
                    log("Дата начала сохранена: " + $session.tourData.startDate);
                    $reactions.answer("Записал дату " + date.toLocaleDateString("ru-RU") + ".");
                    $reactions.transition("/TravelRequest/AskDuration");
                } catch (e) {
                    log("Ошибка даты: " + e.message);
                    $reactions.answer("Не удалось распознать дату. Укажите её в формате ДД.ММ.ГГГГ или словами, например, 'завтра'.");
                    return;
                }
        state: DontKnow
            q: (не знаю/пока не решил/не определился)
            script:
                $session.tourData.startDate = "Не указана";
                log("Дата начала не указана");
            a: Дату можно уточнить позже.
            go!: /TravelRequest/AskDuration

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.tourData.startDate = ($session.tourData.startDate || 0) + 1;
            
            if: $session.tourData.startDate < 3
                random:
                    a: Извините, не совсем понял вас. Какого числа предполагаете выезд?
                    a: К сожалению, не понял вас. На какую дату планируете отправление?
            else: 
                script:
                    $session.tourData.startDate = 0;
                go!: /TravelRequest/AskStartDate/DontKnow

    state: AskDuration
        a: Также укажите, сколько дней будет длиться путешествие.
        intent: /sys/aimylogic/ru/uncertainty || toState = "/TravelRequest/AskDuration/DontKnow"
        event: noMatch || toState = "/TravelRequest/AskDuration/CatchAll"
    
        state: CatchNumber
            q: * (@duckling.number) *
            q: * (@caila.entities.NUMBER) *
            script:
                var duration = parseFloat($parseTree._number || $parseTree.value);
    
                var isInteger = function(num) {
                    return typeof num === 'number' && 
                           isFinite(num) && 
                           Math.floor(num) === num;
                };
    
                if (isNaN(duration) || duration < 1 || !isInteger(duration)) {
                    $reactions.answer("Пожалуйста, укажите целое число дней больше нуля (например, '7' или 'десять').");
                    $reactions.transition("/TravelRequest/AskDuration");
                } else {
                    var startDate;
                    try {
                        startDate = ($session.startDate instanceof Date)
                            ? $session.startDate
                            : new Date($session.startDate);
                        
                        if (isNaN(startDate.getTime())) throw "Invalid date";
                    } catch (e) {
                        // Если дата не задана или некорректна — используем сегодняшнюю
                        startDate = new Date();
                        $session.startDate = startDate.toISOString();
                    }
    
                    var endDate = new Date(startDate);
                    endDate.setDate(startDate.getDate() + duration);
    
                    // Сохраняем в сессию
                    $session.tourData = $session.tourData || {};
                    $session.tourData.duration = duration;
                    $session.tourData.endDate = endDate.toISOString();
                    $session.endDate = endDate.toISOString();
    
                    var getDayPluralForm = function(number) {
                        var lastTwo = number % 100;
                        var lastOne = number % 10;
                        if (lastTwo >= 11 && lastTwo <= 14) return "дней";
                        if (lastOne === 1) return "день";
                        if (lastOne >= 2 && lastOne <= 4) return "дня";
                        return "дней";
                    };
    
                    $reactions.answer("Отлично! Ваше путешествие продлится " + duration + " " + getDayPluralForm(duration) + ".");
                    $reactions.transition("/TravelRequest/AskServices");
                }
    
        state: DontKnow
            intent: /sys/aimylogic/ru/uncertainty
            a: Хорошо, вы можете указать длительность позже. Мы подберём стандартный вариант — 7 дней.
            script:
                var defaultDuration = 7;
                var startDate;
    
                try {
                    startDate = ($session.startDate instanceof Date)
                        ? $session.startDate
                        : new Date($session.startDate);
    
                    if (isNaN(startDate.getTime())) throw "Invalid date";
                } catch (e) {
                    startDate = new Date();
                    $session.startDate = startDate.toISOString();
                }
    
                var endDate = new Date(startDate);
                endDate.setDate(startDate.getDate() + defaultDuration);
    
                $session.tourData = $session.tourData || {};
                $session.tourData.duration = defaultDuration;
                $session.tourData.endDate = endDate.toISOString();
                $session.endDate = endDate.toISOString();
            go!: /TravelRequest/AskServices
    
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.catchAllCounter = ($session.catchAllCounter || 0) + 1;
    
            if: $session.catchAllCounter < 3
                random:
                    a: Извините, не совсем понял вас. Сколько дней планируете быть в поездке?
                    a: К сожалению, не понял вас. На какой срок планируете отъезд?
            else:
                script:
                    $session.catchAllCounter = 0;
                go!: /TravelRequest/AskDuration/DontKnow

    
    state: AskServices
        a: Уточните, пожалуйста, какой пакет услуг вам интересен?
        buttons:
            "Эконом" -> /TravelRequest/AskServices/ProcessPackage/Эконом
            "Стандарт" -> /TravelRequest/AskServices/ProcessPackage/Стандарт
            "VIP" -> /TravelRequest/AskServices/ProcessPackage/VIP
            "Что входит в пакеты?" -> /TravelRequest/AskServices/WhatIsIncluded
        intent: /VIP || toState = "/TravelRequest/AskServices/ProcessPackage/VIP"
        intent: /Эконом || toState = "/TravelRequest/AskServices/ProcessPackage/Эконом"
        intent: /Стандарт || toState = "/TravelRequest/AskServices/ProcessPackage/Стандарт"
        intent: /Price || toState = "/TravelRequest/AskServices/Price"
        event: noMatch || toState = "/TravelRequest/AskServices/CatchAll"

        state: ProcessPackage
            intent: /Пакеты
            
            state: Эконом
                script:
                    $session.tourData = $session.tourData || {};
                        $session.tourData.package = "Эконом";
                        log("DEBUG: Выбран пакет - " + $session.tourData.package);
                a: Пакет "{{$session.tourData.package}}" выбран успешно!
                go!: /TravelRequest/AskName

            state: Стандарт
                script:
                    $session.tourData = $session.tourData || {};
                        $session.tourData.package = "Стандарт";
                        log("DEBUG: Выбран пакет - " + $session.tourData.package);
                a: Пакет "{{$session.tourData.package}}" выбран успешно!
                go!: /TravelRequest/AskName

            state: VIP
                script:
                    $session.tourData = $session.tourData || {};
                        $session.tourData.package = "VIP";
                        log("DEBUG: Выбран пакет - " + $session.tourData.package);
                a: Пакет "{{$session.tourData.package}}" выбран успешно!
                go!: /TravelRequest/AskName

        state: WhatIsIncluded
            intent: /Пакеты
            script:
                var packages = {
                    "Эконом": ["размещение в отеле 3*", "трансфер при заселении"],
                    "Стандарт": ["размещение в отеле 4*", "трансфер при заселении и выезде", "завтраки в отеле"],
                    "VIP": ["размещение в отеле 5*", "трансфер при заселении и выезде", "завтраки, обеды и ужины в отеле", "обзорная экскурсия по городу"]
                };
                
                $reactions.answer("Доступные пакеты:\n\n" +
                    "• Эконом: " + packages["Эконом"].join(", ") + "\n\n" +
                    "• Стандарт: " + packages["Стандарт"].join(", ") + "\n\n" +
                    "• VIP: " + packages["VIP"].join(", "));
            go!: /TravelRequest/AskServices
            
            
        state: DontKnow || noContext = true
            intent: /sys/aimylogic/ru/uncertainty
            a: Мне жаль, но без указания пакета услуг я не смогу отправить заявку. Сделайте выбор, пожалуйста.
            
    
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.servicesCatchAllCounter = ($session.servicesCatchAllCounter || 0) + 1;
            
            if: $session.servicesCatchAllCounter < 3
                random:
                    a:К сожалению, не понял вас. Какой пакет услуг выбираете?
                    a: Извините, не совсем понял вас. Какой пакет услуг вам больше всего подходит?
                buttons:
                    "Эконом" -> /TravelRequest/AskServices/ProcessPackage/Эконом
                    "Стандарт" -> /TravelRequest/AskServices/ProcessPackage/Стандарт
                    "VIP" -> /TravelRequest/AskServices/ProcessPackage/VIP
            else:
                a: К сожалению, без выбора пакета заявка не может быть отправлена. Вы можете вернуться к ее заполнению позже или связаться с нами по номеру 8 (812) 000-00-00.
                script:
                    $session.servicesCatchAllCounter = 0;
                go!: /StartAndEnd/SomethingElse
    
        state: Price
            intent!:/Price 
            a: Cтоимость пакетов:
                • Эконом: 7,000 руб./чел.
                • Стандарт: 12,000 руб./чел.
                • VIP: 20,000 руб./чел.
                    
            # script:
            #     var prices = {
            #         "Эконом": 7000,
            #         "Стандарт": 12000,
            #         "VIP": 20000
            #     };
                
            #     // Определяем, какой пакет спрашивает пользователь
            #     var askedPackage = null;
            #     var userText = $request.query.toLowerCase();
                
            #     if (userText.includes("эконом") || userText.includes("economy")) {
            #         $askedPackage = "Эконом";
            #     } 
            #     else if (userText.includes("стандарт") || userText.includes("standard")) {
            #       $askedPackage = "Стандарт";
            #     }
            #     else if (userText.includes("вип") || userText.includes("vip")) {
            #         $askedPackage = "VIP";
            #     }
                
            #     // Если запрошен конкретный пакет
            #     if (askedPackage) {
            #         var price = prices[askedPackage];
            #         var message = Пакет {{$askedPackage}} стоит {{$price}} рублей на одного человека.;
                    
            #         // Если есть данные о людях и длительности
            #         if ($session.tourData?.people && $session.tourData?.duration && 
            #             $session.tourData.people !== "Не указано" && $session.tourData.duration !== "Не указано") {
                        
            #             var total = price * parseInt($session.tourData.people) * parseInt($session.tourData.duration);
            #             message += \n\nДля вашей поездки (${$session.tourData.people} ${getPeopleWord($session.tourData.people)} на ${$session.tourData.duration} ${getDaysWord($session.tourData.duration)}) общая стоимость составит ${total} рублей.;
            #         }
                    
            #         $reactions.answer(message);
            #     }
            #     // Если пакет не указан - показываем все цены
            #     else {
            #         var allPrices = "Стоимость пакетов:\n\n";
            #         allPrices += "• Эконом: 7,000 руб./чел.\n";
            #         allPrices += "• Стандарт: 12,000 руб./чел.\n";
            #         allPrices += "• VIP: 20,000 руб./чел.\n\n";
            #         allPrices += "Укажите конкретный пакет для расчета полной стоимости (например: 'Сколько стоит VIP?')";
                    
            #         $reactions.answer(allPrices);
            #     }
                
            #     // Функция для склонения слова "человек"
            #     function getPeopleWord(number) {
            #         number = parseInt(number);
            #         var lastDigit = number % 10;
            #         var lastTwo = number % 100;
                    
            #         if (lastTwo >= 11 && lastTwo <= 14) return "человек";
            #         if (lastDigit === 1) return "человек";
            #         if (lastDigit >= 2 && lastDigit <= 4) return "человека";
            #         return "человек";
            #     }
                
            #     // Функция для склонения слова "день"
            #     function getDaysWord(number) {
            #         number = parseInt(number);
            #         var lastDigit = number % 10;
            #         var lastTwo = number % 100;
                    
            #         if (lastTwo >= 11 && lastTwo <= 14) return "дней";
            #         if (lastDigit === 1) return "день";
            #         if (lastDigit >= 2 && lastDigit <= 4) return "дня";
            #         return "дней";
            #     }
            
            go!: /TravelRequest/AskServices

    state: AskName
        a: С параметрами заявки почти закончили! Осталось указать контакты, чтобы менеджер смог связаться с вами. Введите, пожалуйста, ваше имя.
        intent: /sys/aimylogic/ru/name || toState = "/TravelRequest/AskName/Name"
        intent: /sys/aimylogic/ru/uncertainty || toState = "/TravelRequest/AskName/DontKnow"
        event: noMatch || toState = "/TravelRequest/AskName/CatchAll"

        state: Name
            intent: /sys/aimylogic/ru/name
            script:
                var name = $request.query;
                var isCapitalized = /^[А-ЯЁA-Z]/.test(name);
    
                if (isCapitalized) {
                    $client.name = name;
                    $session.tourData.userName = name;
                } else {
                    $session.userName = name;
                    $session.tourData.userName = name;
                }
    
                $reactions.transition("/TravelRequest/AskPhone");
                
        state: DontKnow || noContext = true
            intent: /sys/aimylogic/ru/uncertainty
            a: Мне жаль, но без указания вашего имени отправить заявку не получится. Укажите его, пожалуйста.
    
        state: CatchAll || noContext = true
            script:
                $session.stateCounterInARow = ($session.stateCounterInARow || 0) + 1;
    
            if: $session.servicesCatchAllCounter < 2
                script:
                    $session.userName = $request.query;
                    $reactions.transition("/TravelRequest/UnusualName");
            else: 
                script:
                    $reactions.answer("К сожалению, без указания вашего имени заявка не может быть отправлена. Вы можете вернуться к заполнению позже или связаться с нами по номеру 8(812) 000-00-00.");
                    $reactions.transition("/StartAndEnd/SomethingElse");

    state: UnusualName
        a: Как необычно! Подскажите, вы точно хотели указать в качестве своего имени "{{ $request.query }}"?
        intent: /sys/aimylogic/ru/agreement
        intent: /sys/aimylogic/ru/name
        script:
            $client.name = $session.userName;
            $session.tourData.userName = $session.userName;
        intent: /sys/aimylogic/ru/name || toState = "/TravelRequest/AskName/Name"
        intent: /sys/aimylogic/ru/agreement || toState = "/TravelRequest/AskName/Name"
        intent: /sys/aimylogic/ru/negation || toState = "/TravelRequest/AskName"
        event: noMatch || toState = "/TravelRequest/AskName/Name"

    state: AskPhone
        script:
            if ($client.phone) {
                $session.tourData = $session.tourData || {};
                $session.tourData.phone = $client.phone;
                $reactions.transition("/TravelRequest/AskComment");
            } else {
                $reactions.answer("Укажите номер телефона для связи.");
                 $reactions.buttons([
                {text: "Поделиться контактом", transition: "/TravelRequest/AskPhone/ProcessContact"}
            ]);
            }
        intent: /sys/aimylogic/ru/phone || toState = "/TravelRequest/AskPhone/ProcessPhone"
        event: noMatch || toState = "/TravelRequest/AskPhone/CatchAll"

        state: ProcessPhone
            q: * (@duckling.phone-number | @номерТелефона | мой номер * ) *
            script:
                $session.tourData = $session.tourData || {};
                $session.tourData.phone = $parseTree._phone || $parseTree.text;
                log("Телефон сохранен: " + $session.tourData.phone);
                $reactions.answer("Номер телефона принят!");
                $reactions.transition("/TravelRequest/AskComment");
    

        state: ProcessContact
            event: contactReceived
            script:
                $session.tourData = $session.tourData || {};
                $session.tourData.phone = $request.data.phone;
                log("Телефон из контакта: " + $session.tourData.phone);
                $reactions.answer("Контактные данные получены!");
                $reactions.transition("/TravelRequest/AskComment");

        state: DontKnow || noContext = true
            intent: /sys/aimylogic/ru/uncertainty
            a: Мне жаль, но без указания вашего телефона отправить заявку не получится. Укажите его, пожалуйста.
            
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $session.nameRetryCount = ($session.nameRetryCount || 0) + 1;
        
            if: ($session.nameRetryCount < 2)
                random:
                    a: Извините, не совсем понял вас. Для заявки требуется ваш контактный номер, поэтому, пожалуйста, укажите его.
                    a: К сожалению, не смог распознать номер телефона в вашем ответе. Пожалуйста, укажите его.
    
        
            else: 
                script:
                    $session.nameRetryCount = 0
                    $reactions.answer("К сожалению, без указания вашего номера телефона заявка не может быть отправлена. Вы можете вернуться к заполнению позже или связаться с нами по номеру 8(812) 000-00-00.");
                    $reactions.transition("/StartAndEnd/SomethingElse");

            

    state: AskComment
        script:
            $reactions.answer("Теперь напишите комментарий для менеджера, если это требуется.");
                $reactions.buttons([
            {text: "Не нужно", transition: "/TravelRequest/AskComment/Disagree"}
                ]);
        event: noMatch || toState = "/TravelRequest/AskComment/Comment"
        q: @disagree || toState = "/TravelRequest/AskComment/Disagree"

        state: Comment
            intent: noMatch
            script:
                $session.tourData = $session.tourData || {};
                $session.tourData.userComment = $parseTree.text;
                $reactions.transition("/TravelRequest/Confirmation");
    
        state: Disagree
            intent: /не знаю|не хочу указывать|нет/i
            script:
                $session.tourData = $session.tourData || {};
                $session.tourData.userComment = "Не указано";
                $reactions.transition("/TravelRequest/Confirmation");

    state: Confirmation
        script:
            var formatDate = function(dateStr) {
                if (!dateStr) return "Не указано";
                var date = new Date(dateStr);
                if (isNaN(date.getTime())) return dateStr;
                return date.toLocaleDateString("ru-RU");
            };
            var data = $session.tourData || {};
            log("DEBUG: Содержимое tourData — " + JSON.stringify(data, null, 2));
                var summary = "Все собранные данные по заявке:\n\n";
                summary += "- Имя: " + (data.userName || "Не указано") + "\n";
                summary += "- Телефон: " + (data.phone || "Не указано") + "\n";
                summary += "- Страна: " + (data.country || "Не указано") + "\n";
                summary += "- Кол-во человек: " + (data.people || "Не указано") + "\n";
                summary += "- Дата начала: " + (formatDate(data.startDate) || "Не указано") + "\n";
                summary += "- Дата окончания: " + (formatDate(data.endDate) || "Не указано") + "\n";
                summary += "- Пакет услуг: " + (data.package || "Не указано") + "\n";
                summary += "- Комментарий: " + (data.userComment || "Не указано") + "\n";
                summary += "- Примерная цена: " + (data.price || "Не указана") + "\n";
                $reactions.answer(summary);
                $reactions.answer("Подтвердите, пожалуйста, отправку заявки.");
                $reactions.buttons([
            {text: "Да", transition: "/TravelRequest/Confirmation/Agree"},
            {text: "Нет", transition: "/TravelRequest/Confirmation/Disagree"}
                ]);
        event: noMatch || toState = "/TravelRequest/Confirmation/CatchAll"
        q: @agree || toState = "/TravelRequest/Confirmation/Agree"
        q: @disagree || toState = "/TravelRequest/Confirmation/Disagree"

        state: Agree
            q: * (да|готов|отправляй) *
            script:
                var payload = {
                    clientName: $session.tourData.userName || "Не указано",
                    phone: $session.tourData.phone || "Не указано",
                    country: $session.tourData.country || "Не указано",
                    people: $session.tourData.people || "Не указано",
                    startDate: $session.tourData.startDate || "Не указано",
                    endDate: $session.tourData.endDate || "Не указано",
                    package: $session.tourData.package || "Не указано",
                    comment: $session.tourData.userComment || "Не указано",
                    price: $session.tourData.price || "Не указано"
                };
        
                log("ОТПРАВЛЕНА ЗАЯВКА: " + JSON.stringify(payload));
        
                // Эмуляция запроса
                // $http.query(...) можно вернуть, если есть реальный API
        
                $reactions.answer("Ваша заявка успешно отправлена! Как только наш менеджер выберет самые подходящие для вас варианты, он обязательно с вами свяжется.");
                $reactions.transition("/StartAndEnd/Goodbye");
        
        state: Disagree
            q: * (нет|не хочу|отмена) *
            script:
                $reactions.answer("В таком случае, вы всегда можете вернуться к заполнению заявки повторно или связаться с нами по телефону 8 (812) 000-00-00.");
                $session.tourData = {
                    country: null,
                    people: null,
                    startDate: null,
                    duration: null,
                    endDate: null,
                    package: null,
                    name: null,
                    phone: null,
                    comment: null,
                    price: null
                };
                log("Инициализирована новая структура tourData");    
                $reactions.transition("/StartAndEnd/SomethingElse");
        
        state: CatchAll
            event: noMatch
            script:
                $session.catchAllCount = ($session.catchAllCount || 0) + 1;
        
                if ($session.catchAllCount < 3) {
                    $reactions.answer("Извините, не совсем понял вас. Хотите отправить эту заявку?");
                    $reactions.buttons([
                        {text: "Да", transition: "/TravelRequest/Confirmation/Agree"},
                        {text: "Нет", transition: "/TravelRequest/Confirmation/Disagree"}
                    ]);
                } else {
                    $reactions.answer("К сожалению, так и не смог понять, что имелось в виду. Вы всегда можете вернуться к заполнению заявки повторно или связаться с нами по телефону 8 (812) 000-00-00.");
    
                    $session.tourData = {
                        country: null,
                        people: null,
                        startDate: null,
                        duration: null,
                        endDate: null,
                        package: null,
                        name: null,
                        phone: null,
                        comment: null,
                        price: null
                    };
                    log("Инициализирована новая структура tourData");    
                    $reactions.transition("/StartAndEnd/SomethingElse");
                }