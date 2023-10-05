import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'accordion';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Accordion component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const accordionTitle = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__title');

        expect(accordionTitle).not.null.not.undefined;
    });

    it('should have Accordion item button component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const accordionButton = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__button');

        expect(accordionButton).not.null.not.undefined;
    });

    it('should have expanded attribute if button was clicked', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const accordionButton = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__button');

        await accordionButton.click();

        const expandedAttribute = await $('main .cmp-accordion .cmp-accordion__item').getAttribute('data-cmp-expanded');

        expect(expandedAttribute).not.null.not.undefined;
    });

    it('should have expanded item if button was clicked', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const accordionButton = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__button');

        await accordionButton.click();

        const accordionButtonClassNames = await accordionButton.getAttribute('class');

        expect(accordionButtonClassNames).to.include('cmp-accordion__button--expanded');
    });

    it('should have expanded panel if button was clicked', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const accordionButton = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__button');

        await accordionButton.click();

        const accordionExpandedPanel = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__panel--expanded');

        expect(accordionExpandedPanel).not.null.not.undefined;
    });
});
