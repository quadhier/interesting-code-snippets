import matplotlib.pyplot as plt

# Contributions of hover action
# from https://stackoverflow.com/a/47166787/10302506

line = None
annot = None
fig = None
ax = None

def update_annot(ind):
    x,y = line.get_data()
    xcoord = x[ind["ind"][0]]
    ycoord = y[ind["ind"][0]]
    annot.xy = (xcoord, ycoord)
    text = "{}, {}".format(xcoord, ycoord)
    print('hover at: {} ({}), {}'.format(xcoord, xcoord.hex(), ycoord))
    annot.set_text(text)
    annot.get_bbox_patch().set_alpha(0.4)

def hover(event):
    vis = annot.get_visible()
    if event.inaxes == ax:
        cont, ind = line.contains(event)
        if cont:
            update_annot(ind)
            annot.set_visible(True)
            fig.canvas.draw_idle()
        else:
            if vis:
                annot.set_visible(False)
                fig.canvas.draw_idle()

def read_x_y(infile):
    x = []
    y = []
    with open(infile) as f:
        for line in f.readlines():
            pair = line.split(' ')
            x.append(float(pair[0]))
            y.append(float(pair[1]))
    return (x, y)

if __name__ == '__main__':
    # Fig 1 (Plot with hover action)
    fig, ax = plt.subplots()
    plt.figure(1).suptitle("Rounding Error")

#    x, y = read_x_y('float-error.csv')
#    line = plt.plot(x, y, marker='o', label='float', color='orange')[0]
    x, y = read_x_y('double-error.csv')
    line = plt.plot(x, y, marker='o', label='double', color='green')[0]
    plt.legend()

    annot = ax.annotate("", xy=(0,0), xytext=(-20, 20),
                        textcoords="offset points",
                        bbox=dict(boxstyle="round", fc="w"),
                        arrowprops=dict(arrowstyle="->"))
    annot.set_visible(False)
    fig.canvas.mpl_connect("motion_notify_event", hover)


    # Fig 2
    plt.figure(2).suptitle("Rounding Error")
    plt.yscale('symlog', linthresh=1e-20)

    x, y = read_x_y('float-error.csv')
    plt.plot(x, y, marker='o', label='float', color='orange')
    x, y = read_x_y('double-error.csv')
    plt.plot(x, y, marker='o', label='double', color='green')
    plt.legend()


    # Fig 3
    plt.figure(3).suptitle("Rounding Error")

    x, y = read_x_y('float-error.csv')
    plt.plot(x, y, marker='o', label='float', color='orange')
    x, y = read_x_y('double-error.csv')
    plt.plot(x, y, marker='o', label='double', color='green')
    plt.legend()

    # Fig 4
    plt.figure(4).suptitle("Rounding Error")

    x, y = read_x_y('float-error.csv')
    plt.subplot(211)
    plt.plot(x, y, marker='o', label='float', color='orange')
    plt.legend()

    x, y = read_x_y('double-error.csv')
    plt.subplot(212)
    plt.plot(x, y, marker='o', label='double', color='green')
    plt.legend()

    plt.show()
