import os
import tkinter as tk
from tkinter import ttk

def fill_tree(tree, directory=os.getcwd(), parent=""):
    if parent == "":
        tree.delete(*tree.get_children())
    # Filling the tree with directories and files
    for item in sorted(os.listdir(directory)):
        full_path = os.path.join(directory, item)
        if os.path.isdir(full_path):
            subdirectory = tree.insert(parent, "end", text=item, open=False)
            fill_tree(tree, full_path, subdirectory)
        else:
            tree.insert(parent, "end", text=item, values=[os.path.getsize(full_path)], tags=["file"])
    for dir_item in sorted(os.listdir(directory)):
        full_path = os.path.join(directory, dir_item)
        if os.path.isdir(full_path):
            directory_iid = tree.insert(parent, "end", text=dir_item, open=False, tags=["directory"])
            fill_tree(tree, full_path, directory_iid)

def on_select(event):
    """
    Show the path of the selected item in the status bar.
    """
    selected_item = tree.focus()
    path = tree.item(selected_item, "text")
    for parent_item in tree.parent(selected_item):
        path = tree.item(parent_item, "text") + "/" + path
    status_bar.config(text=path)

root = tk.Tk()
root.geometry("800x600")
root.title("Tree of SHIT")

tree = ttk.Treeview(root)
tree.heading("#0", text="Directory tree for current path: ", anchor="w")
tree.column("#0", minwidth=0, width=400, stretch="yes")
tree.tag_configure("file", background="#f6f6f6")
tree.tag_configure("directory", background="#e6f2ff")

tree_scrollbar = ttk.Scrollbar(root, orient="vertical", command=tree.yview)
tree.configure(yscroll=tree_scrollbar.set)
tree_scrollbar.pack(side="right", fill="y")

status_bar = ttk.Label(root, text="", anchor="w")
status_bar.pack(side="bottom", fill="x")

fill_tree(tree)

tree.bind("<<TreeviewSelect>>", on_select)
tree.pack(fill="both", expand=True)
root.mainloop()