echo --- CLEAN
echo remove /srv/jekyll/.jekyll-cache
rm -rf /srv/jekyll/.jekyll-cache
echo remove /srv/jekyll/_site
rm -rf /srv/jekyll/_site
echo --- NPM
npm install -g sass
echo --- BUNDLE
bundle update bundler install
echo --- GEM
gem sources
echo --- CONFIG
cat -vet /srv/jekyll/_config.yml
echo --- BUILD
jekyll build --watch --force-polling --force_polling --incremental --future --trace