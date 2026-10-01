import { useEffect } from 'react';

/**
 * Custom Hook for Smooth Scroll-Driven Reveal Animations
 * Automatically observes all elements with `.reveal` or `.reveal-scale` classes.
 */
export function useScrollReveal(dependencyTrigger?: unknown) {
  useEffect(() => {
    const observerCallback: IntersectionObserverCallback = (entries, observer) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-revealed');
          // Once revealed, unobserve to maintain performance
          observer.unobserve(entry.target);
        }
      });
    };

    const observer = new IntersectionObserver(observerCallback, {
      root: null,
      threshold: 0.12,
      rootMargin: '0px 0px -50px 0px',
    });

    const elements = document.querySelectorAll('.reveal, .reveal-scale');
    elements.forEach((el) => observer.observe(el));

    return () => {
      observer.disconnect();
    };
  }, [dependencyTrigger]);
}
