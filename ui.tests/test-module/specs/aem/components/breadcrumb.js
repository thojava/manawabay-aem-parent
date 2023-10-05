import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'breadcrumb';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Breadcrumb component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const breadCrumbList = await $('main .cmp-breadcrumb .cmp-breadcrumb__list');

        expect(breadCrumbList).not.null;
    });

    it('should have Breadcrumb list item rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const breadCrumbListItem = await $('main .cmp-breadcrumb .cmp-breadcrumb__list .cmp-breadcrumb__item');

        expect(breadCrumbListItem).not.null;
    });

    it('should have active list item rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const activeListItem = await $('main .cmp-breadcrumb .cmp-breadcrumb__list .cmp-breadcrumb__item.cmp-breadcrumb__item--active');

        expect(activeListItem).not.null;
    });
});
