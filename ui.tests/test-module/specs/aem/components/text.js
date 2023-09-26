import { aem } from '../../../lib/config.js';
import chai from 'chai';
const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'text';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`,  () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have H1 component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        var h1 = await $('.cmp-text h1').getText();

        expect(h1).to.equal('Heading 1');
    });

    it('should have H3 component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        var h3 = await $('.cmp-text h3').getText();

        expect(h3).to.equal('Heading 3');
    });


});
