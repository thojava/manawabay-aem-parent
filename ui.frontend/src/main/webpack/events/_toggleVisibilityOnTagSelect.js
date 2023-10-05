import { EVENT_TYPE } from "./_utils";

(function () {
    "use strict";

    const modules = {
        self: 'data-activation',
        tags: 'toggleComponentOnTag',
    };

    function ToggleVisibilityOnTagSelect(config) {
        if (config && config.element) {
            init();
        }

        function init() {
            const moduleConfig = JSON.parse(config.element.getAttribute(modules.self));

            moduleConfig.forEach(item => {
                const isConsume = item.type === EVENT_TYPE.CONSUME;
                const isToggleTagModule = item.module === modules.tags;

                if (isConsume && isToggleTagModule) {
                    listenWindowEvent(item);
                }
            });
        }

        function listenWindowEvent({ topic, options }) {
            window.addEventListener(topic, (e) => {
                if (e.detail) {
                    toggleVisibility(e.detail, options);
                }
            });
        }

        function toggleVisibility({ payload }, options) {
            const isShow = options.some(option => option.value === payload);

            if (isShow) {
                showComponent();
            } else {
                hideComponent();
            }
        }

        function showComponent() {
            config.element.parentNode.classList.remove('d-none');
        }

        function hideComponent() {
            config.element.parentNode.classList.add('d-none');
        }
    }

    function onDocumentReady() {
        const elements = document.querySelectorAll(`[${modules.self}]`);

        for (let i = 0; i < elements.length; i++) {
            new ToggleVisibilityOnTagSelect({ element: elements[i] });
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
                            const elementsArray = [].slice.call(addedNode.querySelectorAll(`[${modules.self}]`));
                            elementsArray.forEach(function (element) {
                                new ToggleVisibilityOnTagSelect({ element });
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
