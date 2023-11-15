// eslint-disable-next-line @typescript-eslint/no-var-requires
const Flickity = require('flickity');

(function () {
    'use strict';

    const selectors = {
        self: 'list--teaser',
        wrapper: 'list-wrapper',
        slides: 'cmp-list__item',
        cursorButton: 'cmp-list__button-cursor',
        listItemImage: 'cmp-teaser__image',
        progressBar: 'cmp-list__progress-bar',
        barItem: 'cmp-list__progress-bar--item',
        barItemActive: 'cmp-list__progress-bar--item-active',
    };

    const classNames = {
        iconContainer: 'list-icon--container',
        iconElem: 'list-icon--element',
        iconElemActive: 'list-icon--element-active',
        iconImg: 'list-icon--element-image',
    };

    let carouselHeight = 0;

    function CreateIconCursor() {
        const iconImg = document.createElement('img');
        iconImg.classList.add(classNames.iconImg);

        const iconElem = document.createElement('div');
        iconElem.classList.add(classNames.iconElem);
        iconElem.appendChild(iconImg);

        const iconContainer = document.createElement('div');
        iconContainer.classList.add(classNames.iconContainer);
        iconContainer.appendChild(iconElem);

        document.body.prepend(iconContainer);
    }

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
            that._state.initialScrollPosition = window.scrollY;

            cacheElements(config.element);
            createSliderProgressBar();
            initFlickityCarousel();
            initCarouselItemHovering();
            window.addEventListener("scroll", onWindowScroll);
        }

        function initCarouselItemHovering() {
            for (let i = 0; i < Object.values(that._elements.listItems).length; i++) {
                const listItemImage = that._elements.listItems[i].getElementsByClassName(selectors.listItemImage)[0];

                if (listItemImage) {
                    listItemImage.addEventListener('mouseenter', onMouseEnter);
                    listItemImage.addEventListener('mousemove', onMouseMove);
                    listItemImage.addEventListener('mouseleave', onMouseLeave);
                }
            }
        }

        function onWindowScroll() {
            const currentScrollPosition = window.scrollY;
            const scrollDifference = currentScrollPosition - that._state.initialScrollPosition;
            that._state.initialScrollPosition = currentScrollPosition;

            if (!that._state.pageY) {
                return;
            }

            const newPageY = Number(that._state.pageY) + Number(scrollDifference);
            that._state.pageY = newPageY;

            const coords = {
                pageX: that._state.pageX,
                pageY: newPageY,
            };

            onTranslateIcon(coords);
        }

        function onMouseEnter(e) {
            that._state.pageX = e.pageX;
            that._state.pageY = e.pageY;

            onTranslateIcon(e);
            that._elements.iconElem.classList.add(classNames.iconElemActive);
        }

        function onMouseMove(e) {
            that._state.pageX = e.pageX;
            that._state.pageY = e.pageY;

            onTranslateIcon(e);
        }

        function onTranslateIcon({ pageX, pageY }) {
            if (!pageX && !pageY) {
                return;
            }

            const x = pageX - 22;
            const y = pageY - 22;

            that._elements.iconElem.style.transform = `translate(${x}px, ${y}px)`;
        }

        function onMouseLeave() {
            that._elements.iconElem.classList.remove(classNames.iconElemActive);
        }

        function initFlickityCarousel() {
            const elem = that._elements.list;
            const flkty = new Flickity(elem, {
                // options
                cellAlign: 'left',
                prevNextButtons: false,
                pageDots: false,
                accessibility: false,
                resize: true,
                wrapAround: true,
                imagesLoaded: true,
                // events
                on: {
                    change: function (index) {
                        getCurrentSlidePosition(index);
                    },
                    dragMove: function (e) {
                        onMouseMove(e);
                    },
                    ready: function () {
                        if (carouselHeight === 0) {
                            let height = carouselHeight;

                            for (let i = 0; i < Object.values(that._elements.listItems).length; i++) {
                                height = that._elements.listItems[i].clientHeight > height ? that._elements.listItems[i].clientHeight : height;
                            }

                            carouselHeight = height;
                        }
                    }
                }
            });

            flkty.viewport.style.minHeight = `${carouselHeight}px`;

            that._flkty = flkty;
        }

        function getCurrentSlide(temporarySlide) {
            that._state.currentSlide = temporarySlide;
            that._elements.listItems[that._state.currentSlide].scrollIntoView({
                behavior: 'smooth',
                inline: 'start',
                block: 'nearest'
            });
            that._flkty.select(temporarySlide);
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
            that._elements.listItems = that._elements.list.querySelectorAll('li');
            that._elements.iconElem = document.body.getElementsByClassName(classNames.iconElem)[0];
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.self);

        if (Object.values(elements).length > 0) {
            CreateIconCursor();
        }

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

                            if (elementsArray.length > 0) {
                                CreateIconCursor();
                            }

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
