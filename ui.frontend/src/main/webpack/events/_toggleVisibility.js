import { EVENT_TYPE } from "./_utils";

(function () {
    "use strict";

    const modules = {
        self: 'data-activation',
        toggle: 'toggleComponent',
    };

    function ToggleVisibility(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init();
        }

        function init() {
            const moduleConfig = JSON.parse(config.element.getAttribute(modules.self));

            moduleConfig.forEach(item => {
                const isConsume = item.type === EVENT_TYPE.CONSUME;
                const isEmit = item.type === EVENT_TYPE.EMIT;
                const isToggleModule = item.module === modules.toggle;

                if (!item.options && !isToggleModule) {
                    return;
                }

                if (isConsume && isToggleModule) {
                    listenWindowEvent(item);
                }

                if (isEmit && isToggleModule) {
                    that._topic = item.topic;
                    for (let i = 0; i < config.element.children.length; i++) {
                        config.element.children[i].addEventListener('click', () => handleToggle(config.element.children[i], item.options));
                    }
                }
            });
        }

        function listenWindowEvent({ options, topic }) {
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

        function handleToggle(element, options) {
            const payload = options[0].value;

            dispatchCustomEvent(payload);
        }

        function dispatchCustomEvent(payload) {
            const event = new CustomEvent(that._topic, { detail: { payload } });

            window.dispatchEvent(event);
        }
    }

    function onDocumentReady() {
        const elements = document.querySelectorAll(`[${modules.self}]`);

        for (let i = 0; i < elements.length; i++) {
            new ToggleVisibility({ element: elements[i] });
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
                                new ToggleVisibility({ element });
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
