FROM mcr.microsoft.com/vscode/devcontainers/ruby:0-2.7

# Add NodeJS
ARG NODE_VERSION="lts/*"
RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"

# Add bundler and asciidoctor
RUN gem install bundler && \
    gem install asciidoctor

# Add serve
RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g serve"
