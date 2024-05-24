
module.exports = {
  clearMocks: true,
  collectCoverage: true,
  coverageDirectory: "coverage",
  testEnvironment: "jsdom",
  transform: {
    "^.+\\.jsx?$": "babel-jest"
  },
  moduleNameMapper: {
    axios: 'axios/dist/node/axios.cjs',
  },
};
