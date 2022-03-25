import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Data Driven',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        Modonomicon books are defined via json files and can be added/extended in both mods and datapacks.
      </>
    ),
  },
  {
    title: '(Extended) Markdown',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        No need to learn a new markup language - all text styling can be done in markdown.
        Minecraft and Modonomicon-specific functionality is available with extensions to the markdown syntax. 
      </>
    ),
  },
  {
    title: 'Inspired by the best',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        Modonomicon is built with two excellent documentation methods in mind: Thaumcraft's quest-view style Thaumonomicon and the extensible and data-driven system of Patchouli.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
