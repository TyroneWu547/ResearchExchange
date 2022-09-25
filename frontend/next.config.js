const path = require('path');

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  sassOptions: {
    includePaths: [path.join(__dirname, 'styles')]
  },
  images: {
    domains: ["objectstorage.us-ashburn-1.oraclecloud.com"]
  }
}

module.exports = nextConfig