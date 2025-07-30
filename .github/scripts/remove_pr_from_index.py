#!/usr/bin/env python3
import re
import sys
import os
from datetime import datetime

def main():
    pr_number = os.environ.get('PR_NUMBER', 'unknown')

    try:
        with open('/tmp/cleaned-site/index.html', 'r') as f:
            content = f.read()
        
        # Remove the PR card for this specific PR number
        card_pattern = f'<div class="pr-card"[^>]*data-pr="{pr_number}"[^>]*>.*?</div>\s*</div>'
        updated_content = re.sub(card_pattern, '', content, flags=re.DOTALL)
        
        # Check if there are any remaining PR cards
        remaining_cards = re.findall(r'<div class="pr-card"[^>]*data-pr="[^"]*"[^>]*>', updated_content)
        
        if not remaining_cards:
            # No remaining PRs, add a "no previews" message
            no_previews_html = """                          <div class="no-previews" style="text-align: center; color: #666; font-style: italic; grid-column: 1 / -1;">
                              No active PR previews available
                          </div>"""
            
            # Replace any existing content in the grid
            grid_pattern = r'(<div class="pr-grid">)(.*?)(</div>\s*</div>\s*<div style="text-align: center)'
            updated_content = re.sub(grid_pattern, r'\1\n' + no_previews_html + r'\n            \3', updated_content, flags=re.DOTALL)
        
        # Update the last updated timestamp
        timestamp_pattern = r'Last updated: [^<•]*'
        new_timestamp = f'Last updated: {datetime.now().strftime("%Y-%m-%d %H:%M UTC")}'
        updated_content = re.sub(timestamp_pattern, new_timestamp, updated_content)
        
        with open('/tmp/cleaned-site/index.html', 'w') as f:
            f.write(updated_content)
        
        print(f"Successfully removed PR #{pr_number} from index")
        
    except Exception as e:
        print(f"Error updating index: {e}")
        # Don't fail the cleanup if index update fails

if __name__ == "__main__":
    main()