(function () {
    'use strict';

    const BI = 'bi';

    const selectors = {
        self: 'cmp-button__icon',
    };

    function ButtonIcon(config) {

        if (config && config.element) {
            createIcon(config.element);
        }

        const getIconClassName = (fullClassName) => {
            return fullClassName.replace(`${selectors.self}--`, `${BI}-`);
        };

        function createIcon(element) {
            const fullClassName = Object.values(element.classList).find(item => item.includes(`${selectors.self}--`));

            const className = getIconClassName(fullClassName);

            const icon = document.createElement('i');
            icon.classList.add(className);
            element.appendChild(icon);
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.self);
        for (let i = 0; i < elements.length; i++) {
            new ButtonIcon({ element: elements[i] });
        }

        const MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        const body = document.querySelector("body");
        const observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                // needed for IE
                const nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function (addedNode) {
                        if (addedNode.querySelectorAll) {
                            const elementsArray = [].slice.call(addedNode.getElementsByClassName(selectors.self));
                            elementsArray.forEach(function (element) {
                                new ButtonIcon({ element: element });
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });
    }


    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
}());
