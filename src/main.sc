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
        q: * (нет/не знаю/не сейчас/пока нет/вопросов нет) * || toState = "/DontHaveQuestions"

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

    state: DontHaveQuestions
        a: Хорошо! Если будут вопросы - обращайтесь!
        go!: /Goodbye

    state: SomethingElse
        random: 
            a: Хотите спросить что-то еще?
            a: Могу ли я помочь чем-то еще?
            a: Подскажите, у вас остались еще вопросы?
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast
            "Оформить заявку на тур" -> /TravelRequest
        q: да || onlyThisState = true, toState = "/HowCanIHelpYou"
        q: нет || onlyThisState = true, toState = "/DontHaveQuestions"

        state: CatchAll
            event: noMatch
            script:
                $session.somethingElseCounter = ($session.somethingElseCounter || 0) + 1;
            
            if: $session.somethingElseCounter < 3
                random:
                    a: Извините, не совсем понял. Уточните, что вас интересует?
                    a: Не смог разобрать ваш ответ. Выберите один из вариантов:
                buttons:
                    "Погода" -> /WeatherForecast
                    "Тур" -> /TravelRequest
                go!: /SomethingElse
            else:
                a: Простите, так и не смог понять, что вы имели в виду.
                script:
                    $session.somethingElseCounter = 0;
                go!: /Goodbye
        
    state: Goodbye
        random:
            a: Всего доброго!
            a: Всего вам доброго!
            a: Всего доброго, до свидания!
        script:
            $reactions.transition("/")

    state: WeatherForecast
        a: Этот раздел в разработке
        go!: /SomethingElse

    state: TravelRequest
        a: Этот раздел в разработке
        go!: /SomethingElse
        
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
            go!: /HowCanIHelpYou
        else:
            a: К сожалению, я не смог обработать ваш запрос. Пожалуйста, попробуйте позже.
            script:
                $session.globalCounter = 0;
            go!: /SomethingElse

    state: AreYouRobot
        intent!: /ты робот|кто ты|это бот/
        random:
            a: Я Артур — бот-помощник компании Just Tour, всегда готов отвечать на ваши вопросы.
            a: Вы общаетесь с Артуром — чат-ботом, разработанным командой Just Tour, чтобы помогать вам. Всегда рад пообщаться с вами!
        go!: /HowCanIHelpYou
            
    state: WhatCanYouDo
        intent!: /что ты умеешь|твои функции|возможности/
        random:
            a: Умею рассказывать о погоде в городах мира и составлять заявки на подбор подходящего именно вам путешествия.
            a: С удовольствием расскажу вам о ближайших метеопрогнозах для разных городов и помогу составить запрос на подбор тура.
        go!: /HowCanIHelpYou

    state: AnyError
        event!: error
        random:
            a: Извините, произошла техническая ошибка. Специалисты обязательно изучат ее и возьмут в работу. Пожалуйста, напишите в чат позже.
            a: Простите, произошла ошибка в системе. Наши специалисты обязательно ее исправят. Пожалуйста, напишите мне позже.
        buttons:
            "В начало" -> /Start
        script:
            $reactions.transition("/")