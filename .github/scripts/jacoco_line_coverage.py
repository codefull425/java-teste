import sys
import xml.etree.ElementTree as ET


def jacoco_line_coverage_percent(xml_path: str) -> float:
    tree = ET.parse(xml_path)
    root = tree.getroot()

    # JaCoCo has multiple <counter> elements; we want the aggregate (bundle-level).
    # In practice, the last LINE counter in the document corresponds to the bundle total.
    last_line = None
    for counter in root.iter("counter"):
        if counter.get("type") == "LINE":
            last_line = counter

    if last_line is None:
        raise RuntimeError("No LINE counter found in JaCoCo XML.")

    missed = int(last_line.get("missed", "0"))
    covered = int(last_line.get("covered", "0"))
    total = missed + covered
    if total == 0:
        return 100.0
    return (covered * 100.0) / total


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: jacoco_line_coverage.py <path-to-jacoco.xml>", file=sys.stderr)
        return 2

    pct = jacoco_line_coverage_percent(sys.argv[1])
    # Print only the number for easy parsing in shell.
    print(f"{pct:.4f}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
