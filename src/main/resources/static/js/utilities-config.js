// Utilities Configuration
document.addEventListener('DOMContentLoaded', function() {
    // Spacing Utilities
    const spacingElements = document.querySelectorAll('[class*="m-"], [class*="p-"]');
    if (spacingElements.length > 0) {
        spacingElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('m-') || className.startsWith('p-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        const size = parseInt(value);
                        if (!isNaN(size)) {
                            const property = className.startsWith('m-') ? 'margin' : 'padding';
                            const direction = className.split('-')[2] || '';
                            
                            switch (direction) {
                                case 't':
                                    element.style[property + 'Top'] = size * 0.25 + 'rem';
                                    break;
                                case 'b':
                                    element.style[property + 'Bottom'] = size * 0.25 + 'rem';
                                    break;
                                case 'l':
                                    element.style[property + 'Left'] = size * 0.25 + 'rem';
                                    break;
                                case 'r':
                                    element.style[property + 'Right'] = size * 0.25 + 'rem';
                                    break;
                                case 'x':
                                    element.style[property + 'Left'] = size * 0.25 + 'rem';
                                    element.style[property + 'Right'] = size * 0.25 + 'rem';
                                    break;
                                case 'y':
                                    element.style[property + 'Top'] = size * 0.25 + 'rem';
                                    element.style[property + 'Bottom'] = size * 0.25 + 'rem';
                                    break;
                                default:
                                    element.style[property] = size * 0.25 + 'rem';
                            }
                        }
                    }
                }
            });
        });
    }
    
    // Display Utilities
    const displayElements = document.querySelectorAll('[class*="d-"]');
    if (displayElements.length > 0) {
        displayElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('d-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.display = value;
                    }
                }
            });
        });
    }
    
    // Flex Utilities
    const flexElements = document.querySelectorAll('[class*="flex-"]');
    if (flexElements.length > 0) {
        flexElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('flex-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        switch (value) {
                            case 'row':
                                element.style.flexDirection = 'row';
                                break;
                            case 'column':
                                element.style.flexDirection = 'column';
                                break;
                            case 'wrap':
                                element.style.flexWrap = 'wrap';
                                break;
                            case 'nowrap':
                                element.style.flexWrap = 'nowrap';
                                break;
                            case 'justify-start':
                                element.style.justifyContent = 'flex-start';
                                break;
                            case 'justify-end':
                                element.style.justifyContent = 'flex-end';
                                break;
                            case 'justify-center':
                                element.style.justifyContent = 'center';
                                break;
                            case 'justify-between':
                                element.style.justifyContent = 'space-between';
                                break;
                            case 'justify-around':
                                element.style.justifyContent = 'space-around';
                                break;
                            case 'align-start':
                                element.style.alignItems = 'flex-start';
                                break;
                            case 'align-end':
                                element.style.alignItems = 'flex-end';
                                break;
                            case 'align-center':
                                element.style.alignItems = 'center';
                                break;
                            case 'align-stretch':
                                element.style.alignItems = 'stretch';
                                break;
                        }
                    }
                }
            });
        });
    }
    
    // Text Utilities
    const textElements = document.querySelectorAll('[class*="text-"]');
    if (textElements.length > 0) {
        textElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('text-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        switch (value) {
                            case 'left':
                                element.style.textAlign = 'left';
                                break;
                            case 'center':
                                element.style.textAlign = 'center';
                                break;
                            case 'right':
                                element.style.textAlign = 'right';
                                break;
                            case 'justify':
                                element.style.textAlign = 'justify';
                                break;
                            case 'uppercase':
                                element.style.textTransform = 'uppercase';
                                break;
                            case 'lowercase':
                                element.style.textTransform = 'lowercase';
                                break;
                            case 'capitalize':
                                element.style.textTransform = 'capitalize';
                                break;
                            case 'bold':
                                element.style.fontWeight = 'bold';
                                break;
                            case 'normal':
                                element.style.fontWeight = 'normal';
                                break;
                            case 'italic':
                                element.style.fontStyle = 'italic';
                                break;
                            case 'underline':
                                element.style.textDecoration = 'underline';
                                break;
                            case 'line-through':
                                element.style.textDecoration = 'line-through';
                                break;
                            case 'nowrap':
                                element.style.whiteSpace = 'nowrap';
                                break;
                            case 'truncate':
                                element.style.overflow = 'hidden';
                                element.style.textOverflow = 'ellipsis';
                                element.style.whiteSpace = 'nowrap';
                                break;
                        }
                    }
                }
            });
        });
    }
    
    // Visibility Utilities
    const visibilityElements = document.querySelectorAll('[class*="visible-"], [class*="invisible-"]');
    if (visibilityElements.length > 0) {
        visibilityElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('visible-') || className.startsWith('invisible-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.visibility = className.startsWith('visible-') ? 'visible' : 'hidden';
                    }
                }
            });
        });
    }
    
    // Position Utilities
    const positionElements = document.querySelectorAll('[class*="position-"]');
    if (positionElements.length > 0) {
        positionElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('position-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.position = value;
                    }
                }
            });
        });
    }
    
    // Overflow Utilities
    const overflowElements = document.querySelectorAll('[class*="overflow-"]');
    if (overflowElements.length > 0) {
        overflowElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('overflow-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.overflow = value;
                    }
                }
            });
        });
    }
    
    // Shadow Utilities
    const shadowElements = document.querySelectorAll('[class*="shadow-"]');
    if (shadowElements.length > 0) {
        shadowElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('shadow-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        switch (value) {
                            case 'sm':
                                element.style.boxShadow = '0 0.125rem 0.25rem rgba(161, 172, 184, 0.45)';
                                break;
                            case 'lg':
                                element.style.boxShadow = '0 1rem 3rem rgba(161, 172, 184, 0.45)';
                                break;
                            default:
                                element.style.boxShadow = '0 0.5rem 1rem rgba(161, 172, 184, 0.45)';
                        }
                    }
                }
            });
        });
    }
    
    // Width and Height Utilities
    const sizeElements = document.querySelectorAll('[class*="w-"], [class*="h-"]');
    if (sizeElements.length > 0) {
        sizeElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('w-') || className.startsWith('h-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        const size = parseInt(value);
                        if (!isNaN(size)) {
                            const property = className.startsWith('w-') ? 'width' : 'height';
                            element.style[property] = size + '%';
                        }
                    }
                }
            });
        });
    }
    
    // Border Utilities
    const borderElements = document.querySelectorAll('[class*="border-"]');
    if (borderElements.length > 0) {
        borderElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('border-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        switch (value) {
                            case 'top':
                                element.style.borderTop = '1px solid var(--bs-border-color)';
                                break;
                            case 'bottom':
                                element.style.borderBottom = '1px solid var(--bs-border-color)';
                                break;
                            case 'left':
                                element.style.borderLeft = '1px solid var(--bs-border-color)';
                                break;
                            case 'right':
                                element.style.borderRight = '1px solid var(--bs-border-color)';
                                break;
                            default:
                                element.style.border = '1px solid var(--bs-border-color)';
                        }
                    }
                }
            });
        });
    }
    
    // Background Utilities
    const backgroundElements = document.querySelectorAll('[class*="bg-"]');
    if (backgroundElements.length > 0) {
        backgroundElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('bg-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.backgroundColor = 'var(--bs-' + value + ')';
                    }
                }
            });
        });
    }
    
    // Cursor Utilities
    const cursorElements = document.querySelectorAll('[class*="cursor-"]');
    if (cursorElements.length > 0) {
        cursorElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('cursor-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.cursor = value;
                    }
                }
            });
        });
    }
    
    // User Select Utilities
    const userSelectElements = document.querySelectorAll('[class*="user-select-"]');
    if (userSelectElements.length > 0) {
        userSelectElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('user-select-')) {
                    const value = className.split('-')[2];
                    if (value) {
                        element.style.userSelect = value;
                    }
                }
            });
        });
    }
    
    // Pointer Events Utilities
    const pointerElements = document.querySelectorAll('[class*="pe-"]');
    if (pointerElements.length > 0) {
        pointerElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('pe-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        element.style.pointerEvents = value;
                    }
                }
            });
        });
    }
    
    // Z-index Utilities
    const zIndexElements = document.querySelectorAll('[class*="z-"]');
    if (zIndexElements.length > 0) {
        zIndexElements.forEach(function(element) {
            const classes = element.className.split(' ');
            classes.forEach(function(className) {
                if (className.startsWith('z-')) {
                    const value = className.split('-')[1];
                    if (value) {
                        const index = parseInt(value);
                        if (!isNaN(index)) {
                            element.style.zIndex = index;
                        }
                    }
                }
            });
        });
    }
}); 