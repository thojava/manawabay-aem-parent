(function () {
    'use strict';

    const selectors = {
        self: 'cmp-audio',
    };

    function Audio(element) {
        const linkTrigger = element.getElementsByTagName("a")[0];
        if (linkTrigger) {
            linkTrigger.addEventListener("click", function (event) {
                const audio = element.getElementsByTagName("audio")[0];
                audio.play();
                event.preventDefault();
            })
        }
    }


    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.self);

        for (let i = 0; i < elements.length; i++) {
            new Audio(elements[i]);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
}())
