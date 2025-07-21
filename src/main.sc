require: slotfilling/slotFilling.sc
  module = sys.zb-common

theme: /

    state: Start
        q!: $regex</start>
        random: 
            a: Здравствуйте! Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура. 
            a: Приветствую вас! Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
        go!: /HowCanIHelpYou

    state: HowCanIHelpYou
        random: 
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
        buttons:
            "Прогноз погоды" -> /WeatherForecast
            "Оформить заявку" -> /TravelRequest
    
        state: CatchAll
            event: noMatch
            script:
                $session.catchAllCounter = ($session.catchAllCounter || 0) + 1;
            
            if: $session.catchAllCounter < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?
                go!: /HowCanIHelpYou
            else: 
                a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь, совсем скоро научусь отвечать и на него.
                script:
                    $session.catchAllCounter = 0;
                go!: /SomethingElse

    state: WeatherForecast
        q: * (погод*/прогноз*/узнать погоду) *
        script:
            delete $session.city;
            delete $session.date;
        go!: /GetCity

    state: GetCity
        random:
            a: Укажите, пожалуйста, название города
            a: Для какого города показать погоду?
        
        state: UserCity
            q: * @duckling.location *
            script:
                $session.city = $parseTree._location.value;
            if: $session.date
                go!: /CheckDate
            else:
                go!: /GetDate
        
        state: CatchAll
            event: noMatch
            script:
                $session.cityRetryCount = ($session.cityRetryCount || 0) + 1;
            
            if: $session.cityRetryCount < 3
                a: Пожалуйста, укажите город
                go!: /GetCity
            else:
                a: Не удалось распознать город
                script:
                    delete $session.cityRetryCount;
                go!: /SomethingElse

    state: GetDate
        a: На какую дату вас интересует прогноз?
        
        state: UserDate
            q: * @duckling.date *
            script:
                $session.date = $parseTree._date.value;
            if: $session.city
                go!: /CheckDate
            else:
                go!: /GetCity
        
        state: CatchAll
            event: noMatch
            script:
                $session.dateRetryCount = ($session.dateRetryCount || 0) + 1;
            
            if: $session.dateRetryCount < 3
                a: Пожалуйста, укажите дату
                go!: /GetDate
            else:
                a: Не удалось распознать дату
                script:
                    delete $session.dateRetryCount;
                go!: /WeatherForecast

    state: CheckDate
        script:
            $temp.dateObj = new Date($parseTree._date.timestamp);
            $temp.today = new Date();
            
            if ($temp.dateObj < $temp.today) {
                $reactions.answer("Эта дата уже прошла");
                go!: /GetDate;
            }
            else if (($temp.dateObj - $temp.today) > 14*24*60*60*1000) {
                $reactions.answer("Прогноз доступен только на 14 дней");
                go!: /GetDate;
            }
            else {
                go!: /ShowWeather;
            }

    state: ShowWeather
        script:
            $reactions.answer(`Погода в ${$session.city} на ${$session.date}: 25°C, солнечно`);
        go!: /SomethingElse

    state: TravelRequest
        a: Этот раздел в разработке
        go!: /SomethingElse

    state: SomethingElse
        buttons:
            "Узнать погоду" -> /WeatherForecast
            "Оформить заявку" -> /TravelRequest
        go!: /HowCanIHelpYou

    state: DontHaveQuestions
        a: Хорошего дня!
        script:
            $reactions.transition("/");