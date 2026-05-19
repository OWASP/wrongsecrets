BINARY_NAME := sort-contributors
BUILD_DIR := bin
GO := go

.PHONY: build run lint test vet security clean

build:
	mkdir -p $(BUILD_DIR)
	$(GO) build -o $(BUILD_DIR)/$(BINARY_NAME) .

run: build
	./$(BUILD_DIR)/$(BINARY_NAME) $(ARGS)

lint:
	@command -v golangci-lint >/dev/null 2>&1 || { \
		echo "golangci-lint not found. Install from https://golangci-lint.run/usage/install/"; \
		exit 1; \
	}
	golangci-lint run ./...

test:
	$(GO) test ./...

vet:
	$(GO) vet ./...

security:
	@command -v gosec >/dev/null 2>&1 || { \
		echo "gosec not found. Installing with: go install github.com/securego/gosec/v2/cmd/gosec@latest"; \
		GOBIN=$$(pwd)/.tools $(GO) install github.com/securego/gosec/v2/cmd/gosec@latest || exit 1; \
		PATH=$$(pwd)/.tools:$$PATH gosec ./...; \
		exit $$?; \
	}
	gosec ./...

clean:
	rm -rf $(BUILD_DIR) .tools
