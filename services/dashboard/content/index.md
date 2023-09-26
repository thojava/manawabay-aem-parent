---
layout: page
title: AEM Project Services
---

<!-- markdownlint-disable MD025 -->
# {{ page.title }} @ <img src="logos/site.png" alt="site">

<!-- markdownlint-disable MD033 -->
<table class="table table-hover">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">Service</th>
      <th scope="col">Description</th>
      <th scope="col">URLs</th>
      <th scope="col">Direct Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th scope="row"><i class="fa fa-globe"></i></th>
      <td>Environment Links</td>
      <td>Links to services</td>
      <td colspan="2">
        <div class="m-2">
        Author:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.AUTHOR_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="{{ PAGE_META[0] }}"><i class="{{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
        <div class="m-2">
        Consoles:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.CONSOLE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="{{ PAGE_META[0] }}"><i class="{{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
      </td>
    </tr>
    <tr>
      <th scope="row"><img src="logos/aem-design.png" alt="browse" class="logo"></th>
      <td>Author</td>
      <td></td>
      <td>
        <a target="_blank" class="btn btn-primary" href="{{ site.env.AUTHOR_URL }}">Open</a>
        <a target="_blank" class="btn btn-secondary" href="{{ site.env.AUTHOR_URL }}/crx/de">CRX</a>
        <a target="_blank" class="btn btn-secondary" href="{{ site.env.AUTHOR_URL }}/crx/packmgr">Packages</a>
        <a target="_blank" class="btn btn-secondary" href="{{ site.env.AUTHOR_URL }}/system/console">Console</a>
        <a target="_blank" class="btn btn-primary" href="http://localhost:4502/etc/replication/agents.author/publish.html">Replication Agent</a>
        <div class="m-2">
        Pages:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.PAGE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="{{ site.env.AUTHOR_URL }}{{ PAGE_META[0] }}?wcmmode=disabled"><i class="fa {{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
        <div class="m-2">
        Showcase:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.SHOWCASE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="{{ site.env.AUTHOR_URL }}{{ PAGE_META[0] }}?wcmmode=disabled"><i class="fa {{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
      </td>
      <td><a target="_blank" href="http://localhost:{{ site.env.AUTHOR_PORT }}">{{ site.env.AUTHOR_PORT }}</a>, {{ site.env.AUTHOR_DEBUG_PORT }}</td>
    </tr>
    <tr>
      <th scope="row"><img src="logos/aem-design.png" alt="browse" class="logo"></th>
      <td>Publish</td>
      <td></td>
      <td><a target="_blank" class="btn btn-primary" href="{{ site.env.PUBLISH_URL }}">Open</a>
        <a target="_blank" class="btn btn-secondary" href="{{ site.env.PUBLISH_URL }}/crx/de">CRX</a>
        <a target="_blank" class="btn btn-secondary" href="{{ site.env.PUBLISH_URL }}/crx/packmgr">Packages</a>
        <a target="_blank" class="btn btn-secondary" href="{{ site.env.PUBLISH_URL }}/system/console">Console</a>
        <div class="m-2">
        Pages:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.PAGE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="{{ site.env.PUBLISH_URL }}{{ PAGE_META[0] }}"><i class="fa {{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
        <div class="m-2">
        Showcase:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.SHOWCASE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="{{ site.env.PUBLISH_URL }}{{ PAGE_META[0] }}"><i class="fa {{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
      </td>
      <td><a target="_blank" href="http://localhost:{{ site.env.PUBLISH_PORT }}">{{ site.env.PUBLISH_PORT }}</a>, {{ site.env.PUBLISH_DEBUG_PORT }}</td>
    </tr>
    <tr>
      <th scope="row"><img src="logos/Apache_Feather_Logo.svg" alt="browse" class="logo"></th>
      <td>Dispatcher</td>
      <td></td>
      <td><a target="_blank" class="btn btn-primary" href="{{ site.env.DISPATCHER_URL }}">Open</a>
        <div class="m-2">
        Pages:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.PAGE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="https://{{ PAGE_META[3] }}.{{ site.env.DISPATCHER_HOST }}{{ PAGE_META[0] }}"><i class="fa {{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
        <div class="m-2">
        Showcase:
            <div class="row g-2">
                {% assign PAGE_LINKS = site.env.SHOWCASE_LINKS | split: "," %}
                {% for PAGE in PAGE_LINKS -%}
                  {%- if forloop.length > 0 -%}
                    {% assign PAGE_META = PAGE | split: "|" %}
                    <div class="col"><a target="_blank" class="btn btn-warning" href="https://{{ PAGE_META[3] }}.{{ site.env.DISPATCHER_HOST }}{{ PAGE_META[0] }}"><i class="fa {{ PAGE_META[2] }}"></i> {{ PAGE_META[1] }}</a></div>
                    {% unless forloop.last %} {% endunless -%}
                  {%- endif -%}
                {% endfor %}
            </div>
        </div>
      </td>
      <td><a href="{{ site.env.DOMAIN_URL }}:{{ site.env.DISPATCHER_PORT }}">{{ site.env.DISPATCHER_PORT }}</a></td>
    </tr>
    <tr>
      <th scope="row"><img src="logos/squid_logo2.png" alt="browse" class="logo"></th>
      <td>Proxy</td>
      <td>Proxy used by Dispatcher to proxy external services</td>
      <td><a target="_blank" class="btn btn-primary" href="{{ site.env.PROXY_URL }}">Open</a></td>
      <td></td>
    </tr>
    <!--tr>
      <th scope="row"><img src="logos/mongo-express.png" alt="browse" class="logo"></th>
      <td>Mongo UI</td>
      <td>Mongo UI to view contents in Mongo</td>
      <td><a target="_blank" class="btn btn-primary" href="{{ site.env.MONGOUI_URL }}">Open</a></td>
      <td></td>
    </tr>
    <tr>
      <th scope="row"><img src="logos/mongodb.png" alt="browse" class="logo"></th>
      <td>Mongo</td>
      <td>Used by Author instance</td>
      <td></td>
      <td></td>
    </tr-->
    <tr>
      <th scope="row"><img src="logos/Traefik.logo.png" alt="traefik" class="logo"></th>
      <td>Traefik Dashboard</td>
      <td>dashboard</td>
      <td><a target="_blank" class="btn btn-primary" href="{{ site.env.TRAEFIK_URL }}/dashboard/">Open</a>
      </td>
      <td>{{ site.env.TRAEFIK_PORT_DASHBOARD }}, {{ site.env.TRAEFIK_PORT_HTTP }}, {{ site.env.TRAEFIK_PORT_HTTPS }}</td>
    </tr>
    <tr>
      <th scope="row"><img src="logos/nginx_icon.svg" alt="nginx" class="logo"></th>
      <td>Dashboard</td>
      <td>this page</td>
      <td><a target="_blank" class="btn btn-primary" href="{{ site.env.DASHBOARD_URL }}">Open</a></td>
      <td></td>
    </tr> 
    <tr>
      <th scope="row"><i class="fab fa-git"></i></th>
      <td>Git</td>
      <td>Git Repos for this project</td>
      <td>
        <div class="m-2">
        Tools:
            <div class="row g-2">
                <div class="col"><a target="_blank" class="btn btn-primary" href="{{ site.env.GIT_REPO }}"><i class="fab {{ site.env.GIT_REPO_ICON }}"></i> {{ site.env.GIT_REPO_TITLE }}</a></div>
                <div class="col"><a target="_blank" class="btn btn-primary" href="{{ site.env.GIT_REPO_ADOBE }}"><i class="fab {{ site.env.GIT_REPO_ADOBE_ICON }}"></i> {{ site.env.GIT_REPO_ADOBE_TITLE }}</a></div>
                <div class="col"></div>
            </div>
        </div>
      </td>
      <td></td>
    </tr>
  </tbody>
</table>
