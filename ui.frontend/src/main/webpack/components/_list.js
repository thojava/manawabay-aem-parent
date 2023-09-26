(function () {
    'use strict';

    const selectors = {
        self: 'list--teaser',
        slides: 'cmp-list__item',
        buttonPrev: 'cmp-list__button-prev',
        buttonNext: 'cmp-list__button-next',
        buttonImg: 'cmp-list__button-image',
        progressBar: 'cmp-list__progress-bar',
        barItem: 'cmp-list__progress-bar--item',
        barItemActive: 'cmp-list__progress-bar--item-active',
    };

    /**
     * TeaserCarousel Configuration.
     *
     * @typedef {Object} TeaserCarouselConfig Represents an TeaserCarousel configuration
     * @property {HTMLElement} element The HTMLElement representing the TeaserCarousel
     */

    /**
     * TeaserCarousel.
     *
     * @class TeaserCarousel
     * @param {TeaserCarouselConfig} config The TeaserCarousel configuration
     */
    function TeaserCarousel(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init(config);
        }

        function init(config) {
            that._config = config;
            that._state = {};
            that._state.currentSlide = 0;

            cacheElements(config.element);
            createSliderButtons();
            createSliderProgressBar();
        }


        function moveToNext() {
            const temporarySlide = that._state.currentSlide + 1;

            if (temporarySlide > that._elements.listItems.length - 1) {
                getFirstSlide();
            } else {
                getCurrentSlide(temporarySlide);
            }
        }

        function getFirstSlide() {
            that._state.currentSlide = 0;
            that._elements.listItems[that._state.currentSlide].scrollIntoView({
                behavior: 'smooth',
                inline: 'nearest',
                block: 'nearest'
            });
            getCurrentSlidePosition(0);
        }

        function getCurrentSlide(temporarySlide) {
            that._state.currentSlide = temporarySlide;
            that._elements.listItems[that._state.currentSlide].scrollIntoView({
                behavior: 'smooth',
                inline: 'start',
                block: 'nearest'
            });
            getCurrentSlidePosition(temporarySlide);
        }

        function getCurrentSlidePosition(slide) {
            for (let i = 0; i < Object.values(that._elements.progressBarItems).length; i++) {
                if (that._elements.progressBarItems[i].classList.contains(selectors.barItemActive)) {
                    that._elements.progressBarItems[i].classList.remove(selectors.barItemActive);
                }
            }

            that._elements.progressBarItems[slide].classList.add(selectors.barItemActive);
        }

        function createSliderButtons() {
            const buttonNext = document.createElement('button');
            buttonNext.type = 'button';
            buttonNext.classList.add(selectors.buttonNext);
            buttonNext.addEventListener('click', moveToNext);

            const buttonImg = document.createElement('img');
            buttonImg.classList.add(selectors.buttonImg);

            buttonNext.appendChild(buttonImg.cloneNode(true));

            that._elements.self.appendChild(buttonNext);

            that._elements.button = Object.values(that._elements.list.querySelectorAll('button'));
        }

        function createSliderProgressBar() {
            const progressBar = document.createElement('ul');
            progressBar.classList.add(selectors.progressBar);

            const barItem = document.createElement('li');
            barItem.classList.add(selectors.barItem);
            that._elements.progressBarItems = [];

            for (let i = 0; i < Object.values(that._elements.listItems).length; i++) {
                const item = barItem.cloneNode(true);

                if (i === 0) {
                    item.classList.add(selectors.barItemActive);
                }

                item.addEventListener('click', () => getCurrentSlide(i));
                progressBar.appendChild(item);
                that._elements.progressBarItems.push(item);
            }

            that._elements.self.appendChild(progressBar);
        }

        /**
         * Caches the TeaserCarousel elements as defined.
         *
         * @private
         * @param {HTMLElement} wrapper The TeaserCarousel wrapper element
         */
        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
            that._elements.list = wrapper.children[0];
            that._elements.listItems = Object.values(that._elements.list.querySelectorAll('li'));
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.self);
        for (let i = 0; i < elements.length; i++) {
            new TeaserCarousel({ element: elements[i] });
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
                                new TeaserCarousel({ element: element });
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
