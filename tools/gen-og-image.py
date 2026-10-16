#!/usr/bin/env python3
"""Generate the WrongSecrets 1200x630 Open Graph social preview card.

Uses the ORIGINAL WrongSecrets logo (isometric safe with gold bars) from
src/main/resources/static/android-chrome-384x384.png so the card matches the
repo's actual branding, per maintainer review feedback on PR #2768.

No machine-specific paths: run from anywhere, fonts resolve via
WRONGSECRETS_FONT_DIR -> ./fonts -> ~/.fonts -> system font dirs, and the
output PNG is written relative to this script (../static/img/og-image.png).

Requirements: pip install Pillow (>=9). Optional fonts:
Montserrat (Bold/ExtraBold/SemiBold/Regular) in one of the font dirs; falls
back to DejaVu Sans if missing.

Usage:
    python3 gen-og-image.py [--out PATH]
"""
from __future__ import annotations

import argparse
import os
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

# Brand palette (from OWASP WrongSecrets favicon + docs)
NAVY_DARK = (21, 32, 43)
NAVY_MID = (30, 48, 62)
BRAND_BLUE = (93, 123, 147)
BRAND_MID_BLUE = (62, 95, 122)
BRAND_LIGHT_BLUE = (122, 155, 181)
GOLD = (241, 196, 15)
WHITE = (255, 255, 255)
LIGHT_GREY = (180, 198, 214)

WIDTH, HEIGHT = 1200, 630

FONT_NAMES = {
    "extra_bold": "Montserrat-ExtraBold.ttf",
    "bold": "Montserrat-Bold.ttf",
    "semi_bold": "Montserrat-SemiBold.ttf",
    "regular": "Montserrat-Regular.ttf",
}


def font_dirs() -> list[Path]:
    dirs: list[Path] = []
    env = os.environ.get("WRONGSECRETS_FONT_DIR")
    if env:
        dirs.append(Path(env))
    script_dir = Path(__file__).resolve().parent
    dirs.append(script_dir / "fonts")
    dirs.append(Path.home() / ".fonts")
    dirs.append(Path("/usr/share/fonts/truetype/montserrat"))
    dirs.append(Path("/usr/share/fonts/truetype/dejavu"))
    return dirs


def load_font(weight: str, size: int) -> ImageFont.FreeTypeFont:
    for d in font_dirs():
        for candidate in (d / FONT_NAMES[weight], d / FONT_NAMES[weight].lower()):
            if candidate.is_file():
                return ImageFont.truetype(str(candidate), size)
    # Fallback: any DejaVu Sans we can find
    for d in font_dirs():
        for candidate in ("DejaVuSans-Bold.ttf", "DejaVuSans.ttf"):
            p = d / candidate
            if p.is_file():
                return ImageFont.truetype(str(p), size)
    return ImageFont.load_default()


def find_logo() -> Image.Image:
    """Locate the highest-resolution original logo PNG in the repo."""
    candidates = [
        Path(__file__).resolve().parent.parent
        / "src/main/resources/static/android-chrome-384x384.png",
        Path(__file__).resolve().parent.parent
        / "src/main/resources/static/favicon.png",
        Path(__file__).resolve().parent.parent
        / "src/main/resources/static/apple-touch-icon.png",
    ]
    for p in candidates:
        if p.is_file():
            return Image.open(p).convert("RGBA")
    raise FileNotFoundError("Could not locate WrongSecrets logo PNG")


def remove_white_background(img: Image.Image, threshold: int = 245) -> Image.Image:
    """Turn near-white background transparent, keeping the logo's black outline.

    Flood-fill from the edges so interior white areas (if any) survive.
    """
    out = img.copy()
    width, height = out.size
    px = out.load()
    visited = [[False] * height for _ in range(width)]
    stack = []
    for x in range(width):
        for y in (0, height - 1):
            stack.append((x, y))
    for y in range(height):
        for x in (0, width - 1):
            stack.append((x, y))

    def is_white(p):
        return p[0] >= threshold and p[1] >= threshold and p[2] >= threshold

    while stack:
        x, y = stack.pop()
        if x < 0 or y < 0 or x >= width or y >= height:
            continue
        if visited[x][y]:
            continue
        visited[x][y] = True
        r, g, b, a = px[x, y]
        if a < 10 or not is_white((r, g, b)):
            continue
        px[x, y] = (r, g, b, 0)
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            stack.append((x + dx, y + dy))
    return out


def vertical_gradient(size, top, bottom):
    w, h = size
    base = Image.new("RGB", (w, h), top)
    draw = ImageDraw.Draw(base)
    for y in range(h):
        t = y / max(h - 1, 1)
        color = tuple(int(top[i] + (bottom[i] - top[i]) * t) for i in range(3))
        draw.line([(0, y), (w, y)], fill=color)
    return base.convert("RGBA")


def draw_centered(draw, y, text, font, fill):
    bbox = draw.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    return (WIDTH - tw) // 2 - bbox[0], y


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--out", default=None, help="Output PNG path")
    args = parser.parse_args()

    out_path = (
        Path(args.out).resolve()
        if args.out
        else Path(__file__).resolve().parent.parent
        / "src/main/resources/static/img/og-image.png"
    )

    logo = remove_white_background(find_logo())

    # Background: dark navy radial-ish gradient (brand colors)
    bg = vertical_gradient((WIDTH, HEIGHT), NAVY_DARK, NAVY_MID)
    draw = ImageDraw.Draw(bg, "RGBA")

    # Subtle blue glow behind the logo
    glow = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    cx, cy, cr = WIDTH // 2, 105, 340
    for r in range(cr, 0, -6):
        alpha = int(28 * (1 - r / cr))
        gd.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(93, 123, 147, alpha))
    bg.alpha_composite(glow)

    # Gold accent line
    draw.rectangle((0, 0, WIDTH, 6), fill=GOLD)

    # Logo (max 340px tall, centered upper)
    logo_scale = min(340 / logo.height, 1.0)
    logo_scaled = logo.resize(
        (int(logo.width * logo_scale), int(logo.height * logo_scale)),
        Image.LANCZOS,
    )
    lw, lh = logo_scaled.size
    logo_x = (WIDTH - lw) // 2
    logo_y = 92
    bg.alpha_composite(logo_scaled, (logo_x, logo_y))

    # Wordmark
    f_eyebrow = load_font("semi_bold", 30)
    f_title = load_font("extra_bold", 86)
    f_tag = load_font("regular", 34)

    x, y = draw_centered(draw, 440, "OWASP", f_eyebrow, BRAND_LIGHT_BLUE)
    draw.text((x, y), "OWASP", font=f_eyebrow, fill=BRAND_LIGHT_BLUE)

    x, y = draw_centered(draw, 480, "WrongSecrets", f_title, WHITE)
    draw.text((x, y), "WrongSecrets", font=f_title, fill=WHITE)

    x, y = draw_centered(draw, 590, "Find hardcoded secrets in code", f_tag, LIGHT_GREY)
    draw.text((x, y), "Find hardcoded secrets in code", font=f_tag, fill=LIGHT_GREY)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    bg.convert("RGB").save(out_path, optimize=True)
    print(f"wrote {out_path} ({out_path.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
