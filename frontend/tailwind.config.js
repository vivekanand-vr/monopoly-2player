/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
      "./src/**/*.{js,jsx,ts,tsx}",
    ],
    theme: {
      extend: {
        fontFamily: {
          'poppins': ['"Poppins"', 'sans-serif'],
          'jersey-25': ['"Jersey 25"', 'sans-serif'],
        },
      },
    },
    plugins: [],
  }