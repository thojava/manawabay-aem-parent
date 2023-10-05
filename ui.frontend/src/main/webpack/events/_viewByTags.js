import { EVENT_TYPE } from "./_utils";

(function () {
    "use strict";

    const selectors = {
        listItem: 'data-cmp-data-layer'
    };

    const modules = {
        self: 'data-activation',
        tags: 'toggleComponentOnTag',
    };

    const classes = {
        activeTag: 'cmp-list__item-active'
    };

    function Tags(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init();
        }

        function init() {
            const moduleConfig = JSON.parse(config.element.getAttribute(modules.self));

            moduleConfig.forEach(item => {
                const isTagsModule = item.module === modules.tags;
                const isEmit = item.type === EVENT_TYPE.EMIT;

                that._topic = item.topic;

                const hash = location.hash.substring(1).split('&').slice(0, 1).join('');

                if (!item.options) {
                    return;
                }

                if (isTagsModule && isEmit) {
                    if (hash) {
                        activateHashItem(hash, item.options);
                    } else {
                        activateFirstItem(item.options);
                    }

                    for (let i = 0; i < config.element.children.length; i++) {
                        config.element.children[i].addEventListener('click', () => onActiveTagsListItem(config.element.children[i], item.options));
                    }
                }
            });
        }

        function activateFirstItem(options) {
            const listItems = Array.from(config.element.children);

            if (!listItems.some(item => item.classList.contains(classes.activeTag))) {
                onActiveTagsListItem(config.element.children[0], options);
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
            const payload = options.find(option => option.key.toLowerCase() === elementKey).value;

            element.classList.add(classes.activeTag);

            const elementId = Object.keys(JSON.parse(element.getAttribute(selectors.listItem)))[0];
            location.hash = elementId;

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
            new Tags({ element: elements[i] });
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
                                new Tags({ element });
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
