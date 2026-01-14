import openpyxl

wb = openpyxl.load_workbook(r'c:\Users\ferna\Desktop\RepositorioLocal\VIVEL_PROGRAMS_FV\PROCTOR Y DENSIDADES BASE-1.xlsx')

print("=" * 60)
print("DENSIDADES SHEET - STRUCTURE ANALYSIS")
print("=" * 60)
ws = wb['DENSIDADES']

# Get all rows with data
rows_data = []
for row_idx in range(1, 45):
    row_info = []
    for col_idx in range(1, 12):
        cell = ws.cell(row=row_idx, column=col_idx)
        if cell.value is not None:
            is_formula = isinstance(cell.value, str) and cell.value.startswith('=')
            row_info.append({
                'coord': cell.coordinate,
                'value': cell.value,
                'is_formula': is_formula
            })
    if row_info:
        rows_data.append((row_idx, row_info))

for row_idx, cells in rows_data:
    print(f"\nRow {row_idx}:")
    for c in cells:
        marker = "[FORMULA]" if c['is_formula'] else "[MANUAL]"
        val = str(c['value'])[:50]
        print(f"   {c['coord']} {marker}: {val}")
