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
            "Оформить заявку" -> /TravelRequest/Init
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
            if (!$session.tourData) {
                $session.tourData = {
                    country: null,
                    people: null,
                    startDate: null,
                    duration: null,
                    package: null,
                    name: null,
                    phone: null,
                    comment: null,
                    price: null
                };
                log("Инициализирована новая структура tourData");
            }
            log("Текущие данные тура: " + JSON.stringify($session.tourData));
        go!: /TravelRequest/Agree

    state: Agree
        state: Country
            intent: /sys/aimylogic/ru/country
            script:
                $session.tourData.country = $parseTree._country || $parseTree.text;
                log("Страна сохранена: " + $session.tourData.country);
            a: {{$session.tourData.country}} - отличный выбор!
            go!: /TravelRequest/AskNumberOfPeople

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите страну, куда хотите поехать.
            go!: ../Country

    state: Disagree
        a: Хорошо, если передумаете - обращайтесь!
        go!: /StartAndEnd/Goodbye

    state: AskNumberOfPeople
        a: На сколько человек нужен тур?
        
        state: Number
            intent: /sys/aimylogic/ru/number
            script:
                var peopleCount = parseInt($parseTree._number) || parseInt($parseTree.text);
                if (isNaN(peopleCount)) {
                    log("Ошибка преобразования числа людей: " + $parseTree.text);
                    $reactions.answer("Не удалось распознать количество. Пожалуйста, укажите число.");
                    $reactions.transition("../Number");
                } else {
                    $session.tourData.people = peopleCount;
                    log("Количество людей сохранено: " + $session.tourData.people);
                    $reactions.answer("Записал " + $session.tourData.people + " человек.");
                    $reactions.transition("/TravelRequest/AskStartDate");
                }
        
        state: DontKnow
            q: (не знаю/пока не решил/не определился)
            script:
                $session.tourData.people = 1;
                log("Количество людей не указано, установлено по умолчанию: 1");
            a: Будем считать 1 человека.
            go!: /TravelRequest/AskStartDate

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите количество человек цифрой.
            go!: ../Number

    state: AskStartDate
        a: Когда планируете поездку? (ДД.ММ.ГГГГ)
        
        state: Date
            intent: @duckling.date
            script:
                try {
                    if ($parseTree.value && $parseTree.value.value) {
                        var date = new Date($parseTree.value.value);
                        $session.tourData.startDate = date.toISOString();
                        log("Дата начала сохранена: " + $session.tourData.startDate);
                        $reactions.answer("Записал дату " + date.toLocaleDateString("ru-RU") + ".");
                        $reactions.transition("/TravelRequest/AskDuration");
                    } else {
                        throw new Error("Не удалось распознать дату");
                    }
                } catch (e) {
                    log("Ошибка обработки даты: " + e.message);
                    $reactions.answer("Не удалось распознать дату. Пожалуйста, укажите в формате ДД.ММ.ГГГГ.");
                    $reactions.transition("../Date");
                }
        
        state: DontKnow
            q: (не знаю/пока не решил/не определился)
            script:
                $session.tourData.startDate = null;
                log("Дата начала не указана");
            a: Дату можно уточнить позже.
            go!: /TravelRequest/AskDuration

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите дату в формате ДД.ММ.ГГГГ.
            go!: ../Date

    state: AskDuration
        a: На сколько дней нужен тур?
        
        state: Number
            intent: /sys/aimylogic/ru/number
            script:
                var duration = parseInt($parseTree._number) || parseInt($parseTree.text);
                if (isNaN(duration)) {
                    log("Ошибка преобразования длительности тура: " + $parseTree.text);
                    $reactions.answer("Не удалось распознать количество дней. Пожалуйста, укажите число.");
                    $reactions.transition("../Number");
                } else {
                    $session.tourData.duration = duration;
                    $session.tourData.endDate = calculateEndDate();
                    log("Длительность тура сохранена: " + $session.tourData.duration);
                    $reactions.answer("Записал " + $session.tourData.duration + " дней.");
                    $reactions.transition("/TravelRequest/AskServices");
                }
        
        state: DontKnow
            q: (не знаю/пока не решил/не определился)
            script:
                $session.tourData.duration = 7;
                $session.tourData.endDate = calculateEndDate();
                log("Длительность не указана, установлено по умолчанию: 7 дней");
            a: Будем считать стандартные 7 дней.
            go!: /TravelRequest/AskServices

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите количество дней цифрой.
            go!: ../Number

    state: AskServices
        a: Выберите пакет услуг:
        buttons:
            "Эконом (7 000 руб/день)" -> /TravelRequest/AskServices/Package
            "Стандарт (12 000 руб/день)" -> /TravelRequest/AskServices/Package
            "VIP (20 000 руб/день)" -> /TravelRequest/AskServices/Package
            "Что входит в пакеты?" -> /TravelRequest/AskServices/WhatIsIncluded
            "Сколько это будет стоить?" -> /TravelRequest/AskServices/Price
        
        state: Package
            q: (Эконом|Стандарт|VIP)
            script:
                $session.tourData.package = $parseTree.text;
                $session.tourData.price = calculatePrice();
                log("Пакет услуг сохранен: " + $session.tourData.package);
                log("Рассчитанная стоимость: " + ($session.tourData.price || "не определена"));
            a: Пакет "{{$session.tourData.package}}" выбран.
            go!: /TravelRequest/AskName

        state: WhatIsIncluded
            a: В пакет Эконом входит... Стандарт включает... VIP содержит...
            go!: ..

        state: Price
            a: {{$session.tourData.price ? "Примерная стоимость: " + $session.tourData.price + " руб." : "Стоимость зависит от выбранного пакета и продолжительности."}}
            go!: ..

        state: CatchAll
            event: noMatch
            a: Пожалуйста, выберите один из предложенных пакетов.
            go!: /TravelRequest/AskServices

    state: AskName
        a: Ваше имя для заявки?
        
        state: Name
            q: * *
            script:
                $session.tourData.name = $request.query;
                log("Имя сохранено: " + $session.tourData.name);
            a: Спасибо, {{$session.tourData.name}}!
            go!: /TravelRequest/AskPhone

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите ваше имя.
            go!: ../Name

    state: AskPhone
        a: Ваш контактный телефон?
        
        state: Phone
            intent: /sys/aimylogic/ru/phone
            script:
                $session.tourData.phone = $parseTree._phone || $request.query;
                log("Телефон сохранен: " + $session.tourData.phone);
            a: Телефон {{$session.tourData.phone}} записан.
            go!: /TravelRequest/AskComment

        state: CatchAll
            event: noMatch
            a: Пожалуйста, укажите телефон в формате +7XXX XXX-XX-XX.
            go!: ../Phone

    state: AskComment
        a: Хотите добавить комментарий к заявке?
        
        state: Comment
            q: *
            script:
                $session.tourData.comment = $request.query;
                log("Комментарий сохранен: " + $session.tourData.comment);
            a: Комментарий добавлен.
            go!: /TravelRequest/Confirmation

        state: Disagree
            q: (нет/не нужно/без комментария)
            script:
                $session.tourData.comment = null;
                log("Комментарий не добавлен");
            a: Хорошо, комментария не будет.
            go!: /TravelRequest/Confirmation

    state: Confirmation
        script:
            function calculatePrice() {
                if (!$session.tourData.package || !$session.tourData.people || !$session.tourData.duration) {
                    log("Недостаточно данных для расчета стоимости");
                    return null;
                }
                
                var pricePerDay = 0;
                switch($session.tourData.package) {
                    case "Эконом": pricePerDay = 7000; break;
                    case "Стандарт": pricePerDay = 12000; break;
                    case "VIP": pricePerDay = 20000; break;
                    default: return null;
                }
                
                var total = pricePerDay * $session.tourData.people * $session.tourData.duration;
                log("Рассчитана стоимость: " + total + " руб.");
                return total;
            }
    
            function calculateEndDate() {
                if (!$session.tourData.startDate || !$session.tourData.duration) {
                    log("Недостаточно данных для расчета даты окончания");
                    return null;
                }
                try {
                    var date = new Date($session.tourData.startDate);
                    date.setDate(date.getDate() + $session.tourData.duration);
                    log("Дата окончания рассчитана: " + date.toISOString());
                    return date.toISOString();
                } catch (e) {
                    log("Ошибка расчета даты окончания: " + e.message);
                    return null;
                }
            }
    
            function formatConfirmationMessage() {
                var msg = "Проверьте заявку:\n\n" +
                    "Страна: " + ($session.tourData.country || "Не указана") + "\n" +
                    "Количество человек: " + ($session.tourData.people || 1) + "\n" +
                    "Дата: " + ($session.tourData.startDate ? new Date($session.tourData.startDate).toLocaleDateString("ru-RU") : "Не указана") + "\n" +
                    "Продолжительность: " + ($session.tourData.duration || 7) + " дней\n" +
                    "Пакет: " + ($session.tourData.package || "Не выбран") + "\n";
                    
                if ($session.tourData.price) {
                    msg += "Примерная стоимость: " + $session.tourData.price + " руб.\n";
                }
                
                msg += "Имя: " + ($session.tourData.name || "Не указано") + "\n" +
                       "Телефон: " + $session.tourData.phone + "\n\n" +
                       "Все верно?";
                
                log("Сформировано сообщение подтверждения");
                return msg;
            }

        function sendEmail() {
            try {
                var emailText = "Приветствую!\n" +
                    "Это автоматически отправленное ботом Артуром письмо о новой заявке на подбор тура.\n" +
                    "Имя клиента: " + ($session.tourData.name || $session.userName || "Не указано") + "\n" +
                    "Телефон: " + $session.tourData.phone + "\n" +
                    "Желаемая страна пребывания: " + ($session.tourData.country || "Не указана") + "\n" +
                    "Количество людей в поездке: " + ($session.tourData.people || 1) + "\n" +
                    "Приблизительная дата начала поездки: " + 
                        ($session.tourData.startDate ? new Date($session.tourData.startDate).toLocaleDateString("ru-RU") : "Не указана") + "\n" +
                    "Приблизительная дата окончания поездки: " + 
                        ($session.tourData.endDate ? new Date($session.tourData.endDate).toLocaleDateString("ru-RU") : "Не указана") + "\n" +
                    "Желаемый пакет услуг: " + ($session.tourData.package || "Не выбран") + "\n";
                    
                if ($session.tourData.comment) {
                    emailText += "Комментарий клиента: \"" + $session.tourData.comment + "\"\n";
                }
                
                if ($session.tourData.price) {
                    emailText += "Примерная стоимость тура: " + $session.tourData.price + " руб.\n";
                }
                
                $log("Отправка email: " + emailText);
                // $mailer.send("tour-requests@justtour.com", "Новая заявка на тур", emailText);
            } catch (e) {
                $log("Ошибка при формировании email: " + e.message);
            }
        }
        a: {{formatConfirmationMessage()}}
        buttons:
            "Да, все верно" -> /TravelRequest/Confirmation/Agree
            "Нет, исправить" -> /TravelRequest/Confirmation/Disagree
        
        state: Agree
            script:
                sendEmail();
                $session.requestId = "JT-" + Math.floor(Math.random() * 9000 + 1000);
                log("Заявка подтверждена, ID: " + $session.requestId);
            a: Заявка #{{$session.requestId}} оформлена! Менеджер свяжется с вами.
            go!: /StartAndEnd/Goodbye

        state: Disagree
            script:
                log("Пользователь запросил изменение заявки");
            a: Хорошо, давайте исправим. Что нужно изменить?
            go!: /TravelRequest/Init