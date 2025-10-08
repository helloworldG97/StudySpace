# StudyApp Configuration Structure

This directory contains all configuration files for the StudyApp system, organized by component.

## Directory Structure

```
config/
├── ai-processor/
│   └── config.json          # AI/LLM processing configuration
├── database/
│   └── config.json          # Database connection settings
├── api/
│   └── config.json          # API server configuration
├── config_loader.py         # Python configuration loader utility
└── README.md               # This file
```

## Configuration Files

### AI Processor Configuration (`ai-processor/config.json`)
- **llm_endpoint**: URL for the LLM service
- **llm_model**: Model name to use for processing
- **output_dir**: Directory for processed documents
- **supported_formats**: List of supported file formats

### Database Configuration (`database/config.json`)
- **host**: Database server hostname
- **port**: Database server port
- **user**: Database username
- **password**: Database password
- **database**: Database name

### API Configuration (`api/config.json`)
- **host**: API server hostname
- **port**: API server port
- **debug**: Debug mode flag

## Usage

### Python Configuration Loader
Use the `config_loader.py` utility to load all configurations:

```python
from config.config_loader import ConfigLoader

# Load all configurations
loader = ConfigLoader()

# Get specific configurations
db_config = loader.get_database_config()
ai_config = loader.get_ai_processor_config()
api_config = loader.get_api_config()
```

### Direct File Access
You can also load individual configuration files directly:

```python
import json

# Load AI processor config
with open('config/ai-processor/config.json', 'r') as f:
    ai_config = json.load(f)
```

## Migration Notes

- Old `ai-processor/python/config.json` and `ai-processor/config/config.json` have been moved to this new structure
- Update any code references to use the new paths
- Use the `config_loader.py` for centralized configuration management
