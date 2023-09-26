import { aem } from '../../../lib/config.js';
import chai from 'chai';
const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'container';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`,  () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have White container component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const whiteContainer = await $('main .cmp-container .cmp-text').getText();

        expect(whiteContainer).to.equal('White container');
    });
});
