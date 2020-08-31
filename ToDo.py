# -*- coding: utf-8 -*-
from Tkinter import *
import tkFont
import datetime
import Tkinter as tk
import os.path
from tkcalendar import DateEntry
import tkMessageBox
try:
    import ttk as ttk
    import ScrolledText
except ImportError:
    import tkinter.ttk as ttk
    import tkinter.scrolledtext as ScrolledText
import time


class TkinterGUI(tk.Tk):
    
    def __init__(self, *args, **kwargs):
        """
        Create & set window variables.
        """

        tk.Tk.__init__(self, *args, **kwargs)

        self.title("To Do List")


        self.initialize()

    def initialize(self):
        """
        Set window layout.
        """

        self.grid()

        for r in range(3):
            self.rowconfigure(r, weight=1)    
        for c in range(5):
            self.columnconfigure(c, weight=1)

        sf= tkFont.Font(family='Tahoma', size=10)

        self.conversation_lbl1 = Button(self, text='Add New Item', width=20, bg="grey" , command=self.popup_bonus)
        self.conversation_lbl1.grid(column=0, row=0, sticky='nesw', padx=10, pady=10)

        self.conversation_lbl2 = Button(self, text='To Do List', width=20, bg="grey", command=self.populateList)
        self.conversation_lbl2.grid(column=1, row=0, sticky='nesw', padx=10, pady=10)
      
        self.conversation_lbl3 = Button(self, text='Today List', width=10, bg="grey", command=self.todaylist)
        self.conversation_lbl3.grid(column=2, row=0, sticky='nesw', padx=10, pady=10)

        self.conversation_lbl4 = Button(self, text='Completed List', width=10, bg="grey", command=self.closedlist)
        self.conversation_lbl4.grid(column=3, row=0, sticky='nesw', padx=10, pady=10)

        self.description = ScrolledText.ScrolledText(self, width=60,height=25)
        self.description.grid(column=2, row=1, columnspan=3, sticky='nesw')

        self.duedate = Text(self, width=60,height=1)
        self.duedate.grid(column=2, row=2, columnspan=3, sticky='nesw')

        self.listbox = Listbox(self, font=sf, width=40,height=25)
        self.listbox.grid(column=0, row=1, columnspan=2, rowspan=2, sticky='nesw')
        self.listbox.bind('<<ListboxSelect>>', self.onselect)
        self.listbox.bind('<Button-3>', self.popup)
        
        self.populateList()   

    def popup_bonus(self):
        self.win = tk.Toplevel(self)
        self.win.wm_title("Add New Task")

        self.newTitle = ttk.Label(self.win, text='Title', width=10)
        self.newTitle.grid(column=0, row=0, sticky='nesw', padx=10, pady=10)
        
        self.text1 = Text(self.win, width=60,height=1)
        self.text1.grid(column=1, row=0,sticky='nesw', padx=10, pady=10)

        self.newDescription = Label(self.win, text='Description', width=10)
        self.newDescription.grid(column=0, row=1, sticky='nesw', padx=10, pady=10)
        
        self.newText = ScrolledText.ScrolledText(self.win,width=60,height=10)
        self.newText.grid(column=1, row=1,sticky='nesw', padx=10, pady=10)

        self.newDueDate = Label(self.win, text='Due Date', width=10)
        self.newDueDate.grid(column=0, row=2, sticky='nesw', padx=10, pady=10)

        self.dateEntry = DateEntry(self.win, width=12, background='darkblue', foreground='white', borderwidth=2, date_pattern='MM/dd/yyyy')
        self.dateEntry.grid(column=1, row=2, sticky='nesw', padx=10, pady=10)

        self.respond = Button(self.win, text='Submit', command=self.get_response)
        self.respond.grid(column=1, row=3, sticky='nesw', padx=10, pady=10)
 

    def get_response(self):
        userTitle = self.text1.get("1.0","end-1c")
        userText = self.newText.get("1.0","end-1c")
        dueDate = self.dateEntry.get()
        checker = None
        if os.stat("ToDoList.txt").st_size == 0:
            if not userTitle:
                self.win.destroy()
                tkMessageBox.showinfo("Task Title is Empty ", "Provide Task Title and create task")
            else:
                f1 = open("ToDoList.txt", "a")
                f1.write(userTitle+"|"+dueDate+"|open")
                f1.close()
                f2 = open(userTitle+".txt","a+")
                f2.write(userText)
                f2.close()
                self.win.destroy()
        else:
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if not userTitle:
                        checker = False;
                        self.win.destroy()
                        tkMessageBox.showinfo("Task Title is Empty ", "Provide Task Title and create task")
                        break
                    elif userTitle.lower() in line.lower():
                        checker = False;
                        self.win.destroy()
                        tkMessageBox.showinfo("Task already exists ", "Task '"+userTitle + "' already exists. Create with different title")
                        break
                    else:
                        checker = True;
        if checker:
            f1 = open("ToDoList.txt", "a")
            f1.write("\n"+userTitle+"|"+dueDate+"|open")
            f1.close()
            f2 = open(userTitle+".txt","a+")
            f2.write(userText)
            f2.close()
            self.win.destroy()
        self.populateList() 

    def popup(self,event):  
        m = Menu(self, tearoff=0)
        m.add_command(label="Edit", command=self.edit_popup)
        m.add_command(label="Close", command=self.close_popup)
        m.add_command(label="Open", command=self.open_popup)
        m.add_command(label="Delete", command=self.delete_popup)
        
        try:
            m.tk_popup(event.x_root, event.y_root)
        finally:
            m.grab_release()

    def delete_popup(self):
        i = 0;
        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            if os.path.exists(selectedItem+".txt"):
                os.remove(selectedItem+".txt")
            tempFileName = "ToDoList_temp.txt"
            if os.path.exists(tempFileName):
                os.remove(tempFileName)
            ft = open(tempFileName,"a+")
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem not in line:
                        if i == 0:
                            ft.write(line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                            i += 1
                        else:
                            ft.write("\n"+line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                            i += 1               
        f.close()
        ft.close()
        if os.path.exists("ToDoList.txt"):
            os.remove("ToDoList.txt")
        os.rename(tempFileName,"ToDoList.txt")
        self.populateList() 

    def open_popup(self):
        i = 0
        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            tempFileName = "ToDoList_temp.txt"
            if os.path.exists(tempFileName):
                os.remove(tempFileName)
            ft = open(tempFileName,"a+")
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem in line:
                        if i == 0:
                            ft.write(line.split('|')[0]+"|"+line.split('|')[1]+"|open")
                            i += 1
                        else:
                            ft.write("\n"+line.split('|')[0]+"|"+line.split('|')[1]+"|open")
                            i += 1
                    else:
                        if i == 0:
                            ft.write(line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                            i += 1
                        else:
                            ft.write("\n"+line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                            i += 1
        f.close()
        ft.close()
        if os.path.exists("ToDoList.txt"):
            os.remove("ToDoList.txt")
        os.rename(tempFileName,"ToDoList.txt")
        self.populateList() 

    def close_popup(self):
        i = 0
        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            tempFileName = "ToDoList_temp.txt"
            if os.path.exists(tempFileName):
                os.remove(tempFileName)
            ft = open(tempFileName,"a+")
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem in line:
                        if i == 0:
                            ft.write(line.split('|')[0]+"|"+line.split('|')[1]+"|close")
                            i += 1
                        else:
                            ft.write("\n"+line.split('|')[0]+"|"+line.split('|')[1]+"|close")
                            i += 1
                    else:
                        if i == 0:
                            ft.write(line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                            i += 1
                        else:
                            ft.write("\n"+line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                            i += 1
        f.close()
        ft.close()
        if os.path.exists("ToDoList.txt"):
            os.remove("ToDoList.txt")
        os.rename(tempFileName,"ToDoList.txt")
        self.populateList() 

    def edit_popup(self):
   
        self.win1 = tk.Toplevel(self)
        self.win1.wm_title("Add New Task")

        self.newTitle1 = ttk.Label(self.win1, text='Title', width=10)
        self.newTitle1.grid(column=0, row=0, sticky='nesw', padx=10, pady=10)
        
        self.text2 = Text(self.win1, width=60,height=1)
        self.text2.grid(column=1, row=0,sticky='nesw', padx=10, pady=10)

        self.newDescription1 = Label(self.win1, text='Description', width=10)
        self.newDescription1.grid(column=0, row=1, sticky='nesw', padx=10, pady=10)
        
        self.newText1 = ScrolledText.ScrolledText(self.win1,width=60,height=10)
        self.newText1.grid(column=1, row=1,sticky='nesw', padx=10, pady=10)

        self.newDueDate1 = Label(self.win1, text='Due Date', width=10)
        self.newDueDate1.grid(column=0, row=2, sticky='nesw', padx=10, pady=10)

        self.dateEntry1 = DateEntry(self.win1, width=12, background='darkblue', foreground='white', borderwidth=2, date_pattern='MM/dd/yyyy')
        self.dateEntry1.grid(column=1, row=2, sticky='nesw', padx=10, pady=10)

        self.respond1 = Button(self.win1, text='Submit', command=self.edit_response)
        self.respond1.grid(column=1, row=3, sticky='nesw', padx=10, pady=10)

        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem in line:
                        self.text2.insert(tk.END, line.split('|')[0])
                        self.text2.configure(state='disabled')
                        self.dateEntry1.delete(0,"end")
                        self.dateEntry1.insert(tk.END, line.split('|')[1])
                        filename = selectedItem+'.txt'
                        if os.path.exists(filename):
                            f = open(filename,"r")
                            contents = f.read()
                            self.newText1.insert(tk.END, contents)

    def edit_response(self):
        userTitle = self.text2.get("1.0","end-1c")
        userText = self.newText1.get("1.0","end-1c")
        dueDate = self.dateEntry1.get()
        if os.path.exists(userTitle+".txt"):
            os.remove(userTitle+".txt")
        f2 = open(userTitle+".txt","a+")
        f2.write(userText)
        f2.close()
        tempFileName = "ToDoList_temp.txt"
        if os.path.exists(tempFileName):
            os.remove(tempFileName)
        ft = open(tempFileName,"a+")
        i = 0
        with open('ToDoList.txt','r') as f:
            for line in f:
                if userTitle in line:
                    if i == 0:
                        ft.write(line.split('|')[0]+"|"+dueDate+"|"+line.split('|')[2].rstrip('\n'))
                        i += 1
                    else:
                        ft.write("\n"+line.split('|')[0]+"|"+dueDate+"|"+line.split('|')[2].rstrip('\n'))
                        i += 1
                else:
                    if i == 0:
                        ft.write(line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                        i += 1
                    else:
                        ft.write("\n"+line.split('|')[0]+"|"+line.split('|')[1]+"|"+line.split('|')[2].rstrip('\n'))
                        i += 1
        f.close()
        ft.close()
        if os.path.exists("ToDoList.txt"):
            os.remove("ToDoList.txt")
        os.rename(tempFileName,"ToDoList.txt")
        self.win1.destroy()
        self.populateList()

    def onselect(self,event):
        w = event.widget
        idx = int(w.curselection()[0])
        value = w.get(idx)
        self.duedate.delete("1.0","end")
        self.description.delete("1.0","end")
        with open('ToDoList.txt','r') as f:
            for line in f:
                if value in line:
                        self.duedate.insert(tk.INSERT,"Due Date: "+line.split('|')[1])
                        filename = value+'.txt'
                        if os.path.exists(filename):
                            f = open(filename,"r")
                            contents = f.read()
                            self.description.insert(tk.END, contents)

    def todaylist(self):
        self.listbox.delete(0,'end')
        self.duedate.delete("1.0","end")
        self.description.delete("1.0","end")
        with open('ToDoList.txt','r') as f:
            for line in f:
                if "open" in line:
                    CurrentDate = datetime.datetime.now()
                    currentDay = CurrentDate.day
                    currentMonth = CurrentDate.month
                    currentYear = CurrentDate.year
                    todayDate = datetime.date(currentYear, currentMonth, currentDay)
                    expectedMonth = int(line.split('|')[1].rstrip().split('/')[0])
                    expectedDay = int(line.split('|')[1].rstrip().split('/')[1])
                    expectedYear = int(line.split('|')[1].rstrip().split('/')[2])
                    ExpectedDate = datetime.date(expectedYear, expectedMonth, expectedDay)
                    if ExpectedDate == todayDate:
                        self.listbox.insert(tk.END, line.split('|')[0])
        self.listbox.select_set(0)
        curIndex = self.listbox.curselection()
        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem in line:
                        self.duedate.insert(tk.END,"Due Date: "+line.split('|')[1])
                        filename = selectedItem+'.txt'
                        if os.path.exists(filename):
                            f = open(filename,"r")
                            contents = f.read()
                            self.description.insert(tk.END, contents)
        self.conversation_lbl2.configure(bg='grey')
        self.conversation_lbl3.configure(bg='purple')
        self.conversation_lbl4.configure(bg='grey')

    def populateList(self):
        self.listbox.delete(0,'end')
        self.duedate.delete("1.0","end")
        self.description.delete("1.0","end")
        open('ToDoList.txt','a+')
        with open('ToDoList.txt','r') as f:
            for line in f:
                if "open" in line:
                    self.listbox.insert(tk.END, line.split('|')[0])                  
        self.listbox.select_set(0)
        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem in line:
                        self.duedate.insert(tk.END,"Due Date: "+line.split('|')[1])
                        filename = selectedItem+'.txt'
                        if os.path.exists(filename):
                            f = open(filename,"r")
                            contents = f.read()
                            self.description.insert(tk.END, contents)
        self.conversation_lbl2.configure(bg='purple')
        self.conversation_lbl3.configure(bg='grey')
        self.conversation_lbl4.configure(bg='grey')
                            
    def closedlist(self):
        self.listbox.delete(0,'end')
        self.duedate.delete("1.0","end")
        self.description.delete("1.0","end")
        with open('ToDoList.txt','r') as f:
            for line in f:
                if "close" in line:
                    self.listbox.insert(tk.END, line.split('|')[0])
        self.listbox.select_set(0)
        if len(self.listbox.curselection()) > 0:
            selectedItem = self.listbox.get(self.listbox.curselection())
            with open('ToDoList.txt','r') as f:
                for line in f:
                    if selectedItem in line:
                        self.duedate.insert(tk.END,"Due Date: "+line.split('|')[1])
                        filename = selectedItem+'.txt'
                        if os.path.exists(filename):
                            f = open(filename,"r")
                            contents = f.read()
                            self.description.insert(tk.END, contents)
        self.conversation_lbl2.configure(bg='grey')
        self.conversation_lbl3.configure(bg='grey')
        self.conversation_lbl4.configure(bg='purple')
    

gui_example = TkinterGUI()
gui_example.mainloop()
