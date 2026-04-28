// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config

import { themes as prismThemes } from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Modonomicon',
  tagline: 'Thaumonomicon-Style In-Game Documentation',
  url: 'https://klikli-dev.github.io',
  baseUrl: '/modonomicon/',
  onBrokenLinks: 'throw',
  favicon: 'img/favicon.ico',
  organizationName: 'klikli-dev', // Usually your GitHub org/user name.
  projectName: 'modonomicon', // Usually your repo name.

  markdown: {
    hooks: {
      onBrokenMarkdownLinks: 'throw',
      onBrokenMarkdownImages: 'throw',
    },
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          // The sidebar id defined in `sidebars.js` (tutorialSidebar)
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          editUrl: 'https://github.com/klikli-dev/modonomicon/tree/documentation',

          // The default (latest) version label shown in the version dropdown
          // `current` refers to the unversioned docs in /docs (if used)
          includeCurrentVersion: false,
          lastVersion: '26.1.2',
          //If we want to show a version with a specific label, map it here. 
          //Otherwise, the version will be shown as is (e.g. "1.21.1").
          versions: {
              "26.1.2": {
              label: "26.1.2 (Latest)",
              banner: "none"
            },
          }
        },
        // blog: {
        //   showReadingTime: true,
        //   // Please change this to your repo.
        //   editUrl:
        //     'https://github.com/klikli-dev/modonomicon/tree/documentation/blog',
        // },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      algolia: {
        // The application ID provided by Algolia
        appId: 'HARYGHSL7I',

        // Public API key: it is safe to commit it
        apiKey: '46397651f888076243bd99575a933cf0',

        indexName: 'klikli-devio',

        // Optional: see doc section below
        contextualSearch: true,
        // Optional: path for search page that enabled by default (`false` to disable it)
        searchPagePath: 'search',
      },
      navbar: {
        title: 'Modonomicon',
        logo: {
          alt: 'Modonomicon Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Docs',
          },
          {
            type: 'docsVersionDropdown',
            position: 'right',
            // Optionally customize the dropdown text and include current
            // See https://docusaurus.io/docs/versioning for options
            dropdownActiveClassDisabled: true,
            // includeVersions: ['1.21.1','1.20.1'], // not required, automatic from versions.json
          },
          // {to: '/blog', label: 'Blog', position: 'left'},
          {
            href: 'https://github.com/klikli-dev/modonomicon',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Docs',
                to: '/docs/intro',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://dsc.gg/klikli',
              }
            ],
          },
          {
            title: 'More',
            items: [
              // {
              //   label: 'Blog',
              //   to: '/blog',
              // },
              {
                label: 'GitHub',
                href: 'https://github.com/klikli-dev/modonomicon',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} klikli-dev. Built with Docusaurus.`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: ['java', 'groovy'],
      },
    }),


};

module.exports = config;
