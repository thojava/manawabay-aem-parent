import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'download';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Download component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadComponent = await $('.cmp-download');

        expect(downloadComponent).not.null;
    });

    it('should have Download title rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadTitle = await $('.cmp-download .cmp-download__title').getText();

        expect(downloadTitle).to.be.a('string');
    });

    it('should have Download property filename rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadFilename = await $('.cmp-download .cmp-download__property--filename .cmp-download__property-content').getText();

        expect(downloadFilename).to.be.a('string');
    });

    it('should have Download property size rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadSize = await $('.cmp-download .cmp-download__property--size .cmp-download__property-content').getText();

        expect(downloadSize).to.be.a('string');
    });

    it('should have Download property format rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadFormat = await $('.cmp-download .cmp-download__property--format .cmp-download__property-content').getText();

        expect(downloadFormat).to.be.a('string');
    });

    it('should have Download action rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadAction = await $('.cmp-download .cmp-download__action');

        expect(downloadAction).not.null;
    });

    it('should have Download action link rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadActionLink = await $('.cmp-download .cmp-download__action').getProperty('href');

        expect(downloadActionLink).to.be.a('string');
    });

    it('should have Download action text rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const downloadActionText = await $('.cmp-download .cmp-download__action .cmp-download__action-text').getText();

        expect(downloadActionText).to.be.a('string');
    });
});
