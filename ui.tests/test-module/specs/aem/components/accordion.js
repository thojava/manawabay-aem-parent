import { aem } from '../../../lib/config.js';
import chai from 'chai';
const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'accordion';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`,  () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Accordion component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const accordionTitle = await $('main .cmp-accordion .cmp-accordion__item .cmp-accordion__title').getText();

        expect(accordionTitle).to.equal('What are the malls operating hours?');
    });
});
