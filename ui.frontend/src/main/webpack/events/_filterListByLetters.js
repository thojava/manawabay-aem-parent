import { EVENT_TYPE } from "./_utils";

(function () {
    "use strict";

    const selectors = {
        listItem: 'data-cmp-data-layer',
        self: 'data-activation',
    };

    const modules = {
        self: 'filterListByPageTitle',
    };

    const classes = {
        activeTag: 'cmp-list__item-active'
    };

    function FilterListByLetters(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init();
        }

        function init() {
            const moduleConfigs = JSON.parse(config.element.getAttribute(selectors.self));

            moduleConfigs.forEach(module => {
                if (module.module === modules.self) {
                    switch (module.type) {
                        case EVENT_TYPE.EMIT:
                            emitConfig(module);
                            break;

                        case EVENT_TYPE.CONSUME:
                            listenFilterByLetters(module);
                            break;

                        default:
                            break;
                    }
                }
            });
        }

        // Emit Events
        function emitConfig({ options, topic }) {
            if (!options) {
                return;
            }

            that._topic = topic;

            const hash = location.hash.substring(1).split('&').slice(1, 2).join('');

            if (hash) {
                activateHashItem(hash, options);
            } else {
                onActiveTagsListItem(config.element.children[0], options);
            }

            for (let i = 0; i < config.element.children.length; i++) {
                config.element.children[i].addEventListener('click', () => onActiveTagsListItem(config.element.children[i], options));
            }

        }

        function activateHashItem(hash, options) {
            for (let i = 0; i < config.element.children.length; i++) {
                const elementId = Object.keys(JSON.parse(config.element.children[i].getAttribute(selectors.listItem)))[0];

                if (elementId === hash) {
                    onActiveTagsListItem(config.element.children[i], options);
                    config.element.scrollIntoView();
                }
            }
        }

        function clearActiveTags() {
            for (let i = 0; i < config.element.children.length; i++) {
                if (config.element.children[i].classList.contains(classes.activeTag)) {
                    config.element.children[i].classList.remove(classes.activeTag);
                }
            }
        }

        function onActiveTagsListItem(element, options) {
            clearActiveTags();

            const elementKey = element.innerText.trim().toLowerCase();

            const payload = options?.find(option => option.key.toLowerCase() === elementKey)?.value;

            element.classList.add(classes.activeTag);
            updateUrlHash(element);

            dispatchCustomEvent(payload);
        }

        function dispatchCustomEvent(payload) {
            const event = new CustomEvent(that._topic, { detail: { payload } });

            window.dispatchEvent(event);
        }

        function updateUrlHash(item) {
            const itemId = Object.keys(JSON.parse(item.getAttribute(selectors.listItem)))[0];
            const hash = location.hash.substring(1).split('&').slice(0, 1).join('');

            location.hash = `${hash}&${itemId}`;
        }

        // Consume Events
        function listenFilterByLetters({ topic }) {
            window.addEventListener(topic, (e) => {
                if (e.detail) {
                    filterListByLetters(e.detail);
                }
            });
        }

        function filterListByLetters({ payload }) {
            const keys = getLettersRange(payload);

            for (let i = 0; i < config.element.children.length; i++) {
                const isIncluded = keys.some(key => key.toLowerCase() === config.element.children[i].innerText.trim().charAt(0).toLowerCase());

                if (isIncluded) {
                    config.element.children[i].classList.remove('d-none');
                } else {
                    config.element.children[i].classList.add('d-none');
                }
            }
        }

        function getLettersRange(payload) {
            let start = 'a';
            let stop = 'z';

            if (payload.toLowerCase() !== 'all') {
                start = payload.split('-').slice(0, 1).join('');
                stop = payload.split('-').slice(1, 2).join('');
            }

            const result = [];
            for (let idx = start.charCodeAt(0), end = stop.charCodeAt(0); idx <= end; ++idx) {
                result.push(String.fromCharCode(idx));
            }
            return result;
        }
    }

    function onDocumentReady() {
        const elements = document.querySelectorAll(`[${selectors.self}]`);

        for (let i = 0; i < elements.length; i++) {
            new FilterListByLetters({ element: elements[i] });
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
                            const elementsArray = [].slice.call(addedNode.querySelectorAll(`[${selectors.self}]`));
                            elementsArray.forEach(function (element) {
                                new FilterListByLetters({ element });
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
