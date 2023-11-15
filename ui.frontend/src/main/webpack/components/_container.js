(function () {
    'use strict';

    const selectors = {
        self: 'container-animation__light-off',
    };

    const colors = {
        black: '#000000',
        white: '#ffffff'
    };

    function AnimationContainer(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init(config);
        }

        function init(config) {
            that._config = config;
            that._state = {};
            that.windowHeight = window.innerHeight;
            cacheElements(config.element);

            that._state.initialBackground = window.getComputedStyle(that._elements.self).backgroundColor;
            that._state.initialColor = window.getComputedStyle(that._elements.self).color;

            that._elements.self.style.backgroundColor = colors.white;
            that._elements.self.style.color = colors.black;

            window.addEventListener('scroll', createContainerAnimation);
        }

        function createContainerAnimation() {
            const y = that._elements.self.getBoundingClientRect().y;

            const difference = that.windowHeight - y;

            if (difference < 100) {
                that._elements.self.style.backgroundColor = colors.white;
                that._elements.self.style.color = colors.black;
            } else if (difference > 100) {
                that._elements.self.style.backgroundColor = that._state.initialBackground;
                that._elements.self.style.color = that._state.initialColor;
            }
        }

        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.self);
        for (let i = 0; i < elements.length; i++) {
            new AnimationContainer({ element: elements[i] });
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
                                new AnimationContainer({ element: element });
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
