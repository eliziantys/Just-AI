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
            "Прогноз погоды"
            "Оформить заявку"
        state: CatchAll
            event: noMatch
            script:
                if (!context.stateCounterInARow) {
                    context.stateCounterInARow = 1;
                } else {
                    context.stateCounterInARow += 1;
                }
                if (context.stateCounterInARow < 3) {
                     random:
                a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь? : 
                a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?;
                    $reactions.answer(randomPhrase);
                } else {
                    a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь, совсем скоро научусь отвечать и на него.
                    go!: /SomethingElse;