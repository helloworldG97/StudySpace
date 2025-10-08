#!/usr/bin/env python3
"""
Configuration loader for StudyApp
Loads and merges configuration from multiple JSON files
"""

import json
import os
from typing import Dict, Any

class ConfigLoader:
    """Loads and manages configuration from multiple JSON files"""
    
    def __init__(self, base_config_dir: str = "config"):
        """Initialize the configuration loader"""
        self.base_config_dir = base_config_dir
        self.config = {}
        self.load_all_configs()
    
    def load_all_configs(self):
        """Load all configuration files and merge them"""
        config_files = {
            'ai-processor': os.path.join(self.base_config_dir, 'ai-processor', 'config.json'),
            'database': os.path.join(self.base_config_dir, 'database', 'config.json'),
            'api': os.path.join(self.base_config_dir, 'api', 'config.json')
        }
        
        for config_type, config_path in config_files.items():
            if os.path.exists(config_path):
                try:
                    with open(config_path, 'r') as f:
                        config_data = json.load(f)
                        self.config.update(config_data)
                        print(f"Loaded {config_type} configuration from {config_path}")
                except Exception as e:
                    print(f"Error loading {config_type} configuration: {e}")
            else:
                print(f"Warning: Configuration file not found: {config_path}")
    
    def get_config(self, key: str = None):
        """Get configuration value(s)"""
        if key is None:
            return self.config
        return self.config.get(key)
    
    def get_database_config(self):
        """Get database configuration"""
        return self.config.get('database', {})
    
    def get_ai_processor_config(self):
        """Get AI processor configuration"""
        return {
            'llm_endpoint': self.config.get('llm_endpoint'),
            'llm_model': self.config.get('llm_model'),
            'output_dir': self.config.get('output_dir'),
            'supported_formats': self.config.get('supported_formats')
        }
    
    def get_api_config(self):
        """Get API configuration"""
        return self.config.get('api', {})

# Example usage
if __name__ == "__main__":
    loader = ConfigLoader()
    print("Database config:", loader.get_database_config())
    print("AI Processor config:", loader.get_ai_processor_config())
    print("API config:", loader.get_api_config())
