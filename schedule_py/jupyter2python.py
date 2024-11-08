import nbformat

# 读取 Jupyter Notebook 文件
notebook_path = "schedule_py\schedule.ipynb"
with open(notebook_path, "r", encoding="utf-8") as f:
    notebook = nbformat.read(f, as_version=4)

# 提取所有单元格内容并转换为 Python 脚本格式
script_lines = []
for cell in notebook.cells:
    if cell.cell_type == "code":
        script_lines.append(cell.source + "\n\n")

# 写入 Python 脚本文件
script_path = "schedule_py\schedule.py"
with open(script_path, "w", encoding="utf-8") as f:
    f.writelines(script_lines)

print(f"Python script has been saved to {script_path}")