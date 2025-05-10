// Animations Configuration
document.addEventListener('DOMContentLoaded', function() {
    // Fade In Animation
    const fadeInElements = document.querySelectorAll('.fade-in');
    if (fadeInElements.length > 0) {
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('show');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1
        });
        
        fadeInElements.forEach(function(element) {
            observer.observe(element);
        });
    }
    
    // Slide In Animation
    const slideInElements = document.querySelectorAll('.slide-in');
    if (slideInElements.length > 0) {
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('show');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1
        });
        
        slideInElements.forEach(function(element) {
            observer.observe(element);
        });
    }
    
    // Scale In Animation
    const scaleInElements = document.querySelectorAll('.scale-in');
    if (scaleInElements.length > 0) {
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('show');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1
        });
        
        scaleInElements.forEach(function(element) {
            observer.observe(element);
        });
    }
    
    // Rotate In Animation
    const rotateInElements = document.querySelectorAll('.rotate-in');
    if (rotateInElements.length > 0) {
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('show');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1
        });
        
        rotateInElements.forEach(function(element) {
            observer.observe(element);
        });
    }
    
    // Bounce In Animation
    const bounceInElements = document.querySelectorAll('.bounce-in');
    if (bounceInElements.length > 0) {
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('show');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1
        });
        
        bounceInElements.forEach(function(element) {
            observer.observe(element);
        });
    }
    
    // Shake Animation
    const shakeElements = document.querySelectorAll('.shake');
    if (shakeElements.length > 0) {
        shakeElements.forEach(function(element) {
            element.addEventListener('click', function() {
                this.classList.add('shaking');
                setTimeout(function() {
                    element.classList.remove('shaking');
                }, 500);
            });
        });
    }
    
    // Pulse Animation
    const pulseElements = document.querySelectorAll('.pulse');
    if (pulseElements.length > 0) {
        pulseElements.forEach(function(element) {
            element.addEventListener('click', function() {
                this.classList.add('pulsing');
                setTimeout(function() {
                    element.classList.remove('pulsing');
                }, 1000);
            });
        });
    }
    
    // Spin Animation
    const spinElements = document.querySelectorAll('.spin');
    if (spinElements.length > 0) {
        spinElements.forEach(function(element) {
            element.addEventListener('click', function() {
                this.classList.add('spinning');
                setTimeout(function() {
                    element.classList.remove('spinning');
                }, 1000);
            });
        });
    }
    
    // Loading Animation
    const loadingElements = document.querySelectorAll('.loading');
    if (loadingElements.length > 0) {
        loadingElements.forEach(function(element) {
            element.addEventListener('click', function() {
                this.classList.add('loading');
                setTimeout(function() {
                    element.classList.remove('loading');
                }, 2000);
            });
        });
    }
    
    // Progress Animation
    const progressElements = document.querySelectorAll('.progress');
    if (progressElements.length > 0) {
        progressElements.forEach(function(element) {
            const progressBar = element.querySelector('.progress-bar');
            if (progressBar) {
                const value = progressBar.getAttribute('aria-valuenow');
                if (value) {
                    progressBar.style.width = '0%';
                    setTimeout(function() {
                        progressBar.style.width = value + '%';
                    }, 100);
                }
            }
        });
    }
    
    // Typing Animation
    const typingElements = document.querySelectorAll('.typing');
    if (typingElements.length > 0) {
        typingElements.forEach(function(element) {
            const text = element.textContent;
            element.textContent = '';
            element.classList.add('typing');
            
            let i = 0;
            const typeWriter = function() {
                if (i < text.length) {
                    element.textContent += text.charAt(i);
                    i++;
                    setTimeout(typeWriter, 100);
                } else {
                    element.classList.remove('typing');
                }
            };
            
            typeWriter();
        });
    }
    
    // Blink Animation
    const blinkElements = document.querySelectorAll('.blink');
    if (blinkElements.length > 0) {
        blinkElements.forEach(function(element) {
            element.addEventListener('click', function() {
                this.classList.add('blinking');
                setTimeout(function() {
                    element.classList.remove('blinking');
                }, 1000);
            });
        });
    }
    
    // Hover Effects
    const hoverElements = document.querySelectorAll('.hover-effect');
    if (hoverElements.length > 0) {
        hoverElements.forEach(function(element) {
            element.addEventListener('mouseenter', function() {
                this.classList.add('hover');
            });
            
            element.addEventListener('mouseleave', function() {
                this.classList.remove('hover');
            });
        });
    }
    
    // Transition Effects
    const transitionElements = document.querySelectorAll('.transition-effect');
    if (transitionElements.length > 0) {
        transitionElements.forEach(function(element) {
            element.addEventListener('click', function() {
                this.classList.add('transitioning');
                setTimeout(function() {
                    element.classList.remove('transitioning');
                }, 500);
            });
        });
    }
    
    // Animation Classes
    const animationElements = document.querySelectorAll('[data-animation]');
    if (animationElements.length > 0) {
        animationElements.forEach(function(element) {
            const animation = element.getAttribute('data-animation');
            const duration = element.getAttribute('data-duration') || '1s';
            const delay = element.getAttribute('data-delay') || '0s';
            const iteration = element.getAttribute('data-iteration') || '1';
            
            element.style.animation = `${animation} ${duration} ${delay} ${iteration}`;
        });
    }
    
    // Animation Duration
    const durationElements = document.querySelectorAll('[data-duration]');
    if (durationElements.length > 0) {
        durationElements.forEach(function(element) {
            const duration = element.getAttribute('data-duration');
            if (duration) {
                element.style.animationDuration = duration;
            }
        });
    }
    
    // Animation Delay
    const delayElements = document.querySelectorAll('[data-delay]');
    if (delayElements.length > 0) {
        delayElements.forEach(function(element) {
            const delay = element.getAttribute('data-delay');
            if (delay) {
                element.style.animationDelay = delay;
            }
        });
    }
    
    // Animation Iteration
    const iterationElements = document.querySelectorAll('[data-iteration]');
    if (iterationElements.length > 0) {
        iterationElements.forEach(function(element) {
            const iteration = element.getAttribute('data-iteration');
            if (iteration) {
                element.style.animationIterationCount = iteration;
            }
        });
    }
}); 