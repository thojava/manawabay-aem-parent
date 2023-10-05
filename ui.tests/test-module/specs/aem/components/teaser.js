import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'teaser';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Teaser component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const teaserComponent = await $('main .cmp-teaser');

        expect(teaserComponent).not.null;
    });

    it('should have Teaser image rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const teaserImage = await $('main .cmp-teaser .cmp-teaser__image .cmp-image .cmp-image__image');

        expect(teaserImage).not.null;
    });

    it('should have Teaser image source rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const teaserImageSrc = await $('main .cmp-teaser .cmp-teaser__image .cmp-image .cmp-image__image').getProperty('src');

        expect(teaserImageSrc).not.null.not.undefined;
    });

    it('should have Teaser content rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const teaserContent = await $('main .cmp-teaser .cmp-teaser__content');

        expect(teaserContent).not.null;
    });

    it('should have Teaser title rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const teaserTitle = await $('main .cmp-teaser .cmp-teaser__content .cmp-teaser__title');

        expect(teaserTitle).not.null.not.undefined;
    });
});
