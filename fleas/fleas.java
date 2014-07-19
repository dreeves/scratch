import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class fleas extends Applet {
    public void stop()
    {
        killall();
        ended = 1;
    }

    public void loadaudio()
    {
        tscr = 4;
        mes = new Button("Please Wait Loading Sounds ...");
        mes.setFont(f);
        mes.reshape(202, 279, 239, 30);
        add(mes);
        antigrav.play();
        antigrav.stop();
        dopercent(45);
        balloon.play();
        balloon.stop();
        bomb.play();
        bomb.stop();
        dopercent(50);
        bridge.play();
        bridge.stop();
        button.play();
        button.stop();
        dopercent(55);
        click.play();
        click.stop();
        deadsnd.play();
        deadsnd.stop();
        dopercent(60);
        dead2.play();
        dead2.stop();
        exitsnd.play();
        exitsnd.stop();
        dopercent(65);
        spring.play();
        spring.stop();
        teleport.play();
        teleport.stop();
        dopercent(70);
        remove(mes);
        tscr = 3;
    }

    public void addblock(int x, int y)
    {
        if(y < 336 && y > 0 && x > 0 && x < 640)
        {
            int xb = x / 16;
            int yb = y / 16;
            int xb4 = xb * 4;
            int yb4 = yb * 4;
            for(x = xb4; x < xb4 + 4; x++)
                for(y = yb4; y < yb4 + 4; y++)
                    if(lvl[x][y] != 0)
                        return;


            playsnd(click);
            map[xb][yb] = (char)(sel + 1);
            fixblock(xb, yb);
            for(x = xb4; x < xb4 + 4; x++)
                for(y = yb4; y < yb4 + 4; y++)
                {
                    if(sel == 1)
                        lvl[x][y] = 1;
                    if(sel == 2 && x - xb4 == 3 - (y - yb4))
                        lvl[x][y] = 1;
                    if(sel == 3 && x - xb4 == y - yb4)
                        lvl[x][y] = 1;
                }


        }
    }

    public void dopercent(int n)
    {
        percent = n;
        Graphics g = getGraphics();
        g.setColor(Color.red);
        g.fillRect(170, 176 + dpy, 304, 32);
        g.setColor(Color.black);
        g.fillRect(171, 177 + dpy, 302, 30);
        g.setColor(Color.red);
        g.fillRect(172, 178 + dpy, percent * 3, 28);
        g.setColor(Color.white);
        g.setFont(f);
        g.drawString("Now Loading - " + percent + "%", 200, 200 + dpy);
    }

    public void fixblock2(int xb, int yb)
    {
        reblock(xb, yb);
        for(int n = 0; n < wno; n++)
        {
            int a = map[wx[n]][wy[n]];
            if(a == wa[n] || a == wb[n])
            {
                int xd = wx[n] - xb;
                if(xd < 0)
                    xd = -xd;
                int yd = wy[n] - yb;
                if(yd < 0)
                    yd = -yd;
                if(xd < 2 && yd < 2)
                {
                    int x = wx[n];
                    int y = wy[n];
                    a = wa[n];
                    int b = wb[n];
                    map[x][y] = (char)a;
                    reblock(x, y);
                    buf = anim1[n].getGraphics();
                    buf.drawImage(blbuf, 0, 0, this);
                    map[x][y] = (char)b;
                    reblock(x, y);
                    buf = anim2[n].getGraphics();
                    buf.drawImage(blbuf, 0, 0, this);
                    buf = bufpic.getGraphics();
                }
            }
        }

    }

    public void dospecial(int x, int y, int di, int n)
    {
        int t = 0;
        if(map[x][y] == '\n')
        {
            playsnd(bomb);
            for(int a = x - 1; a <= x + 1; a++)
            {
                for(int b = y - 1; b <= y + 1; b++)
                {
                    map[a][b] = '\0';
                    fixblock(a, b);
                    for(int c = 0; c < 4; c++)
                    {
                        for(int d = 0; d < 4; d++)
                            lvl[a * 4 + c][b * 4 + d] = 0;

                    }

                }

            }

        }
        if(map[x][y] == '\005')
        {
            playsnd(balloon);
            map[x][y] = '\0';
            fixblock(x, y);
            fs[n] = 1;
            for(int c = 0; c < 4; c++)
            {
                for(int d = 0; d < 4; d++)
                    lvl[x * 4 + c][y * 4 + d] = 0;

            }

        }
        if(map[x][y] == '\006' && di == 2)
        {
            fs[n] = 2;
            playsnd(spring);
        }
        if(map[x][y] == '\007' && di == 2 && gy == 0)
        {
            int b = nospid;
            for(int a = 0; a < nospid; a++)
                if(spidx[a] == x && spidy[a] == y)
                    b = a;

            spidd[b] = 4;
            if(b == nospid)
            {
                spidx[b] = x;
                spidy[b] = y;
                spidp[b] = 0;
                map[x][y - 2] = '\036';
                fixblock(x, y - 2);
                nospid++;
                buf = bufpic.getGraphics();
                buf.copyArea(x * 16, (y - 2) * 16 + spidp[b], 20, 20, b * 20 - x * 16, 412 - (y - 2) * 16 - spidp[b]);
            }
        }
        if(map[x][y] == '\r' && di != 3)
        {
            dead[n] = 1;
            playdead();
        }
        if(map[x][y] == '\033' && di != 3)
        {
            dead[n] = 1;
            playdead();
        }
        if(map[x][y] == '\020' || map[x][y] == '\035')
        {
            int dx;
            int dy;
            if(x == tx1 && y == ty1)
            {
                dx = tx2;
                dy = ty2;
            } else
            {
                dx = tx1;
                dy = ty1;
            }
            if(map[dx][dy] == '\020' || map[dx][dy] == '\035')
            {
                if(fxd[n] > 0 && lvl[dx * 4 + 4][dy * 4] <= 0)
                {
                    fx[n] = dx * 16 + 16;
                    fy[n] = dy * 16;
                    playsnd(teleport);
                }
                if(fxd[n] < 0 && lvl[dx * 4 - 1][dy * 4] <= 0)
                {
                    fx[n] = dx * 16 - 4;
                    fy[n] = dy * 16;
                    playsnd(teleport);
                }
            }
        }
        if(map[x][y] == '\021')
        {
            dead[n] = 1;
            exit++;
            playsnd(exitsnd);
        }
        if(map[x][y] == '\023' && gco == 0)
        {
            playsnd(antigrav);
            g1 = -g1;
            g4 = -g4;
            g16 = -g16;
            gy = 4 - gy;
            gco = 3;
            if(di < 2)
                fxd[n] = -fxd[n];
        }
        if(map[x][y] == '\025' && di < 2)
        {
            playsnd(bridge);
            map[x][y] = '\0';
            fixblock(x, y);
            fxd[n] = -fxd[n];
            for(int c = 0; c < 4; c++)
            {
                for(int d = 0; d < 4; d++)
                    lvl[x * 4 + c][y * 4 + d] = 0;

            }

        }
        t = 0;
        if(map[x][y] == '\026')
            t = 1;
        if(map[x][y] == '\032')
            t = 1;
        if(t == 1 && di == 2)
        {
            int a = x - fxd[n] / 4;
            if(map[a][y] == '\026')
            {
                playsnd(bridge);
                map[a][y] = '\0';
                fixblock(a, y);
                for(int c = 0; c < 4; c++)
                {
                    for(int d = 0; d < 4; d++)
                        lvl[a * 4 + c][y * 4 + d] = 0;

                }

            }
        }
        if(map[x][y] == '\027')
        {
            playsnd(button);
            for(int a = 0; a < 40; a++)
            {
                for(int b = 0; b < 21; b++)
                    if(map[a][b] == '\027' || map[a][b] == '\024')
                    {
                        map[a][b] = '\0';
                        fixblock(a, b);
                        for(int c = 0; c < 4; c++)
                        {
                            for(int d = 0; d < 4; d++)
                                lvl[a * 4 + c][b * 4 + d] = 0;

                        }

                    }

            }

        }
        if(map[x][y] == '\030')
        {
            playsnd(button);
            for(int a = 0; a < 40; a++)
            {
                for(int b = 0; b < 21; b++)
                {
                    if(map[a][b] == '\030')
                    {
                        map[a][b] = '\0';
                        fixblock(a, b);
                        for(int c = 0; c < 4; c++)
                        {
                            for(int d = 0; d < 4; d++)
                                lvl[a * 4 + c][b * 4 + d] = 0;

                        }

                    }
                    if(map[a][b] == '\022')
                    {
                        map[a][b] = '\002';
                        fixblock(a, b);
                        for(int c = 0; c < 4; c++)
                        {
                            for(int d = 0; d < 4; d++)
                                lvl[a * 4 + c][b * 4 + d] = 1;

                        }

                    }
                }

            }

        }
        if(map[x][y] == '\031' && di != 3)
        {
            dead[n] = 1;
            playdead();
        }
    }

    public void paint(Graphics g)
    {
        buf = bufpic.getGraphics();
        showStatus("Applet Flea Circus Running");
        if(tscr == 10)
        {
            g.setColor(Color.white);
            g.fillRect(0, 0, 650, 400);
            g.setColor(Color.black);
            g.fillRect(56, 55, 528, 272);
            g.drawImage(win, 64, 63, this);
            return;
        }
        if(tscr > 0)
        {
            buf.setColor(Color.white);
            buf.fillRect(0, 0, 650, 400);
            buf.setColor(Color.black);
            buf.fillRect(56, 55, 528, 272);
            buf.drawImage(title, 64, 63, this);
            if(showpc == 1)
            {
                buf.setColor(Color.red);
                buf.fillRect(170, 176 + dpy, 304, 32);
                buf.setColor(Color.black);
                buf.fillRect(171, 177 + dpy, 302, 30);
                buf.setColor(Color.red);
                buf.fillRect(172, 178 + dpy, percent * 3, 28);
                buf.setColor(Color.white);
                buf.setFont(f);
                buf.drawString("Now Loading - " + percent + "%", 200, 200 + dpy);
            }
            g.drawImage(bufpic, 0, 0, this);
            if(tscr < 2)
            {
                tscr++;
                try
                {
                    Thread.sleep(40L);
                }
                catch(InterruptedException e)
                {
                    showStatus(e.toString());
                }
                repaint();
            }
            return;
        }
        if(pstate == 1)
        {
            g.drawImage(bufpic, 0, 0, this);
            return;
        }
        animco = (animco + 1) % 8;
        for(int n = 0; n < wno; n++)
        {
            int i = map[wx[n]][wy[n]];
            if(i == wa[n] || i == wb[n])
                if(wm[n] == 0)
                {
                    if(animco == 3)
                        buf.drawImage(anim1[n], wx[n] * 16, wy[n] * 16, this);
                    if(animco == 7)
                        buf.drawImage(anim2[n], wx[n] * 16, wy[n] * 16, this);
                } else
                if(wa[n] == 9)
                {
                    int xf = wx[n] * 16;
                    int yf = wy[n] * 16;
                    int spin = 0;
                    for(i = 0; i < no; i++)
                        if(dead[i] == 0 && fx[i] >= xf && fy[i] >= yf && fx[i] < xf + 16 && fy[i] < yf + 16)
                            spin = 1;

                    if(spin == 1)
                        buf.drawImage(anim1[n], xf, yf, this);
                    else
                        buf.drawImage(anim2[n], xf, yf, this);
                }
        }

        if(no < fno)
        {
            tic--;
            if(tic < 1)
            {
                tic = 6;
                no++;
            }
        }
        for(int n = 0; n < no; n++)
        {
            if(dead[n] == 0 && fs[n] != 1 && map[fx[n] / 16][fy[n] / 16] == '\002')
            {
                dead[n] = 1;
                playdead();
                buf.copyArea(n * 16, 396, 4, 4, (fx[n] + 4) - n * 16, (fy[n] + gy2) - 396);
            }
            if(dead[n] == 0)
            {
                int xf = fx[n];
                int yf = fy[n];
                int xdf = fxd[n];
                int xf4 = xf / 4;
                int yf4 = yf / 4;
                int xdf4 = xdf / 4;
                if(fs[n] != 1)
                    buf.copyArea(n * 16, 396, 4, 4, (xf + 4) - n * 16, (yf + gy2) - 396);
                if(xdf < 0)
                    fr[n] = animco % 2;
                else
                    fr[n] = 2 + animco % 2;
                int infan = 0;
                for(int i = 0; i < nofans; i++)
                    if(xf >= fanx1[i] && xf < fanx2[i] && yf >= fany1[i] && yf < fany2[i] && fand[i] == 0)
                    {
                        if(xf / 16 > fanx[i])
                        {
                            fxd[n] = 4;
                            xdf = 4;
                            xdf4 = 1;
                        } else
                        {
                            fxd[n] = -4;
                            xdf = -4;
                            xdf4 = -1;
                        }
                        infan = 1;
                    }

                if(fs[n] != 0)
                {
                    if(fs[n] == 1)
                    {
                        buf.copyArea(n * 16, 396, 16, 20, xf - 2 - n * 16, yf - 16 - 396);
                        yf -= g4;
                        int x = xf / 16;
                        int y = yf / 16;
                        if(map[x][y] == '\013' && yf % 16 == 4)
                        {
                            fs[n] = 0;
                            fxd[n] = -4;
                            xdf = -4;
                        }
                    }
                    if(fs[n] == 2)
                    {
                        lvl[xf4][yf4] = 0;
                        if(lvl[xf4][yf4 - g1] == 2)
                            dospecial(xf4 / 4, (yf4 - 1) / 4, 3, n);
                        xf = fx[n];
                        yf = fy[n];
                        xdf = fxd[n];
                        xf4 = xf / 4;
                        yf4 = yf / 4;
                        xdf4 = xdf / 4;
                        yf -= g4;
                        yf4 -= g1;
                        if(lvl[xf4][yf4] > 0 || infan != 0)
                        {
                            yf += g4;
                            yf4 += g1;
                            fs[n] = 0;
                        }
                        lvl[xf4][yf4] = 1;
                    }
                } else
                {
                    lvl[xf4][yf4] = 0;
                    if(lvl[xf4][yf4 + g1] == 2 && infan == 0)
                        dospecial(xf4 / 4, (yf4 + g1) / 4, 2, n);
                    if(lvl[xf4][yf4 - g1] == 2 && infan == 0)
                        dospecial(xf4 / 4, (yf4 - g1) / 4, 3, n);
                    if((lvl[xf4][yf4 + g1] > 0 || infan != 0) && lvl[xf4 + xdf4][yf4] == 2)
                        dospecial((xf4 + xdf4) / 4, yf4 / 4, xdf4, n);
                    xf = fx[n];
                    yf = fy[n];
                    xdf = fxd[n];
                    xf4 = xf / 4;
                    yf4 = yf / 4;
                    xdf4 = xdf / 4;
                    if(fs[n] != 2)
                        if(lvl[xf4][yf4 + g1] <= 0 && infan == 0)
                            yf += g4;
                        else
                        if(lvl[xf4 + xdf4][yf4] > 0 && lvl[xf4][yf4 - g1] <= 0 && lvl[xf4 + xdf4][yf4 - g1] <= 0)
                        {
                            xf += xdf;
                            yf -= g4;
                        } else
                        if(lvl[xf4 + xdf4][yf4] <= 0)
                            xf += xdf;
                        else
                            xdf = -xdf;
                    xf4 = xf / 4;
                    yf4 = yf / 4;
                    if(fs[n] != 1 && dead[n] == 0)
                        lvl[xf4][yf4] = 1;
                }
                fx[n] = xf;
                fy[n] = yf;
                fxd[n] = xdf;
            }
        }

        for(int n = 0; n < nospid; n++)
        {
            int x = spidx[n];
            int y = spidy[n];
            int xf = x * 16;
            int yf = (y - 2) * 16 + spidp[n];
            for(int i = 0; i < no; i++)
                if(fx[i] >= xf - 4 && fx[i] < xf + 20 && fy[i] >= yf && fy[i] < yf + 20)
                {
                    if(dead[i] == 0)
                    {
                        dead[i] = 1;
                        playdead();
                    }
                    buf.copyArea(i * 16, 396, 4, 4, (fx[i] + 4) - i * 16, fy[i] - 396);
                    lvl[fx[i] / 4][fy[i] / 4] = 0;
                }

            buf.copyArea(n * 20, 412, 20, 20, x * 16 - n * 20, ((y - 2) * 16 + spidp[n]) - 412);
            spidp[n] += spidd[n];
            if(spidp[n] >= 16)
            {
                spidp[n] = 16;
                spidd[n] = -4;
            }
            if(spidp[n] == 0)
            {
                map[x][y - 2] = '\017';
                fixblock(x, y - 2);
                for(int i = n; i < nospid; i++)
                {
                    spidx[i] = spidx[i + 1];
                    spidy[i] = spidy[i + 1];
                    spidp[i] = spidp[i + 1];
                    spidd[i] = spidd[i + 1];
                    buf.copyArea((i + 1) * 20, 412, 20, 20, -20, 0);
                }

                nospid--;
            }
        }

        for(int n = 0; n < nofix; n++)
            fixblock2(fixx[n], fixy[n]);

        nofix = 0;
        gy2 = gy;
        if(gco > 0)
            gco--;
        for(int n = 0; n < fno; n++)
            if(fs[n] == 1)
                buf.copyArea(fx[n] - 2, fy[n] - 16, 16, 20, n * 16 - (fx[n] - 2), 396 - (fy[n] - 16));
            else
            if(dead[n] == 0)
                buf.copyArea(fx[n] + 4, fy[n] + gy2, 4, 4, n * 16 - (fx[n] + 4), 396 - (fy[n] + gy2));

        for(int n = 0; n < nospid; n++)
        {
            int x = spidx[n];
            int y = spidy[n];
            buf.copyArea(x * 16, (y - 2) * 16 + spidp[n], 20, 20, n * 20 - x * 16, 412 - (y - 2) * 16 - spidp[n]);
        }

        for(int n = 0; n < no; n++)
            if(fs[n] == 1)
            {
                buf.drawImage(bl[5], fx[n] - 4, fy[n] - 16, this);
                buf.drawImage(flea[fr[n]], fx[n] + 4, fy[n], this);
            } else
            if(dead[n] == 0)
                buf.drawImage(flea[fr[n]], fx[n] + 4, fy[n] + gy2, this);

        for(int n = 0; n < nospid; n++)
        {
            int x = spidx[n];
            int y = spidy[n];
            buf.drawImage(bl[15], x * 16, (y - 2) * 16 + spidp[n], this);
        }

        g.drawImage(bufpic, 0, 0, this);
        if(mbut == 1)
            addblock(mx, my);
        if(exit == sav[levno])
        {
            levno++;
            if(levno == numlev)
            {
                killall();
                tscr = 10;
                ended = 1;
                repaint();
            } else
            {
                loadlevel();
                drawlevel();
                initfleas();
            }
        }
        if(ended == 0)
        {
            while(timer.timer == 1) 
                try
                {
                    Thread.sleep(5L);
                }
                catch(InterruptedException e)
                {
                    showStatus(e.toString());
                }
            timer.timer = 1;
            repaint();
        }
    }

    public fleas()
    {
        dpy = 180;
        showpc = 0;
        percent = 0;
        bl = new Image[31];
        flea = new Image[4];
        tscr = 1;
        pstate = 0;
        hei = 28;
        fx = new int[40];
        fy = new int[40];
        fxd = new int[40];
        dead = new int[40];
        fs = new int[40];
        fr = new int[40];
        ended = 0;
        no = 40;
        fno = 40;
        exit = 0;
        sel = 1;
        xbp = 32;
        map = new char[40][21];
        lvl = new int[160][100];
        wno = 0;
        wx = new int[50];
        wy = new int[50];
        wm = new int[50];
        wa = new int[50];
        wb = new int[50];
        animco = 0;
        spidx = new int[10];
        spidy = new int[10];
        spidp = new int[10];
        spidd = new int[10];
        fanx = new int[5];
        fany = new int[4];
        fand = new int[4];
        fanx1 = new int[5];
        fany1 = new int[5];
        fanx2 = new int[5];
        fany2 = new int[5];
        tno = 0;
        g1 = 1;
        g4 = 4;
        g16 = 16;
        gy = 0;
        gy2 = 0;
        gco = 0;
        nofix = 0;
        fixx = new int[1000];
        fixy = new int[1000];
        anim1 = new Image[50];
        anim2 = new Image[50];
        num = new int[50];
        sav = new int[50];
        pass = new String[50];
        levstr = new char[40000];
    }

    public boolean mouseUp(Event e, int x, int y)
    {
        mx = x;
        my = y;
        mbut = 0;
        return true;
    }

    public void makeanim(int x, int y, int a, int b)
    {
        wx[wno] = x;
        wy[wno] = y;
        wa[wno] = a;
        wb[wno] = b;
        wm[wno] = 0;
        map[x][y] = (char)a;
        reblock(x, y);
        buf = anim1[wno].getGraphics();
        buf.drawImage(blbuf, 0, 0, this);
        map[x][y] = (char)b;
        reblock(x, y);
        buf = anim2[wno].getGraphics();
        buf.drawImage(blbuf, 0, 0, this);
        wno++;
    }

    public void destroy()
    {
        killall();
        ended = 1;
    }

    public void initfleas()
    {
        buf = bufpic.getGraphics();
        for(int n = 0; n < 40; n++)
        {
            fx[n] = ex;
            fy[n] = ey;
            fxd[n] = -4;
            dead[n] = 0;
            fs[n] = 0;
            no = 0;
            tic = 200;
            fr[n] = 0;
            lvl[fx[n] / 4][fy[n] / 4] = 1;
            buf.copyArea(fx[n] + 4, fy[n], 4, 4, n * 16 - (fx[n] + 4), 396 - fy[n]);
        }

    }

    public void reblock(int xb, int yb)
    {
        buf = bufpic.getGraphics();
        buf2 = blbuf.getGraphics();
        buf2.setColor(Color.black);
        buf2.fillRect(0, 0, 20, 20);
        buf2.drawImage(bl[map[xb + 1][yb + 1]], 16, 16, this);
        buf2.drawImage(bl[map[xb][yb + 1]], 0, 16, this);
        buf2.drawImage(bl[map[xb - 1][yb + 1]], -16, 16, this);
        buf2.drawImage(bl[map[xb + 1][yb]], 16, 0, this);
        buf2.drawImage(bl[map[xb][yb]], 0, 0, this);
        buf2.drawImage(bl[map[xb - 1][yb]], -16, 0, this);
        buf2.drawImage(bl[map[xb + 1][yb - 1]], 16, -16, this);
        buf2.drawImage(bl[map[xb][yb - 1]], 0, -16, this);
        buf2.drawImage(bl[map[xb - 1][yb - 1]], -16, -16, this);
        buf.drawImage(blbuf, xb * 16, yb * 16, this);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void playsnd(AudioClip sample)
    {
        sample.play();
    }

    public void playdead()
    {
        int rno = (int)(Math.random() * 128D);
        if(rno > 64)
            playsnd(deadsnd);
        else
            playsnd(dead2);
    }

    public void grablevs()
    {
        tscr = 4;
        mes = new Button("Please Wait Loading Levels ...");
        mes.setFont(f);
        mes.reshape(202, 279, 239, 30);
        add(mes);
        dopercent(0);
        try
        {
            fileURL = new URL(getCodeBase(), "levels.lev");
            input = fileURL.openStream();
            dataInput = new DataInputStream(input);
            String text = dataInput.readLine();
            numlev = 0;
            for(int i = 1; i < text.length(); i++)
                numlev = numlev * 10 + (text.charAt(i) - 48);

            for(int n = 0; n < numlev; n++)
            {
                pass[n] = dataInput.readLine();
                for(int i = 30; i < 64; i++)
                    pass[n] = pass[n].replace((char)i, (char)(i + 30));

                if(pword.equalsIgnoreCase(pass[n]))
                    levno = n;
            }

            dopercent(10);
            for(int n = 0; n < numlev; n++)
            {
                text = dataInput.readLine();
                num[n] = 0;
                for(int i = 1; i < text.length(); i++)
                    num[n] = num[n] * 10 + (text.charAt(i) - 48);

            }

            dopercent(20);
            for(int n = 0; n < numlev; n++)
            {
                text = dataInput.readLine();
                sav[n] = 0;
                for(int i = 1; i < text.length(); i++)
                    sav[n] = sav[n] * 10 + (text.charAt(i) - 48);

            }

            dopercent(30);
            text = dataInput.readLine();
            for(int i = 0; i < numlev * 840; i++)
            {
                levstr[i] = text.charAt(i);
                if(levstr[i] == '\372')
                    levstr[i] = '\n';
                if(levstr[i] == '\373')
                    levstr[i] = '\r';
            }

            dataInput.close();
        }
        catch(MalformedURLException e)
        {
            showStatus("Exception: " + e.toString());
        }
        catch(IOException e)
        {
            showStatus("Exception: " + e.toString());
        }
        remove(mes);
        tscr = 3;
        dopercent(40);
    }

    public void start()
    {
        showpc = 0;
        tscr = 1;
        ended = 0;
        but1 = new Button("Start New Game");
        but1.setFont(f);
        but1.reshape(202, 279, 100, 30);
        add(but1);
        but2 = new Button("Continue Game");
        but2.setFont(f);
        but2.reshape(341, 279, 100, 30);
        add(but2);
        repaint();
    }

    public void killall()
    {
        if(tscr == 2 || tscr == 1)
        {
            remove(but1);
            remove(but2);
            tscr = 3;
        }
        if(tscr == 4)
        {
            remove(mes);
            tscr = 3;
        }
        if(tscr == 0)
        {
            remove(quit);
            remove(restart);
            remove(pause);
            tscr = 3;
        }
        if(tscr == 5)
        {
            remove(prompt1);
            remove(prompt2);
            tscr = 3;
        }
    }

    public void calcfans()
    {
        for(int n = 0; n < nofans; n++)
        {
            int x = fanx[n];
            int y = fany[n];
            int x1 = x * 16;
            int y1 = y * 16;
            int x2 = x * 16 + 20;
            int y2 = y * 16 + 16;
            if(map[x][y] != '\016' && map[x][y] != '\034')
                fand[n] = 1;
            for(int i = x - 1; map[i][y] == 0;)
            {
                i--;
                x1 -= 16;
            }

            for(int i = x + 1; map[i][y] == 0;)
            {
                i++;
                x2 += 16;
            }

            fanx1[n] = x1;
            fany1[n] = y1;
            fanx2[n] = x2;
            fany2[n] = y2;
        }

    }

    public boolean action(Event e, Object o)
    {
        if(e.target == but1)
        {
            remove(but1);
            remove(but2);
            tscr = 3;
            pword = "";
            newgame();
        }
        if(e.target == but2)
        {
            remove(but1);
            remove(but2);
            tscr = 5;
            contgame();
        }
        if(e.target == restart && pstate == 0)
        {
            loadlevel();
            drawlevel();
            initfleas();
        }
        if(e.target == pause)
        {
            pstate = 1 - pstate;
            remove(pause);
            if(pstate == 0)
                pause = new Button("Pause");
            else
                pause = new Button("Unpause");
            pause.setFont(f);
            pause.reshape(480, 355, 50, 25);
            add(pause);
            repaint();
        }
        if(e.target == quit && pstate == 0)
        {
            stop();
            start();
        }
        if(e.target == prompt2)
        {
            killall();
            tscr = 3;
            pword = prompt2.getText();
            newgame();
        }
        return true;
    }

    public boolean mouseDown(Event e, int x, int y)
    {
        mx = x;
        my = y;
        if(pstate == 0)
        {
            mbut = 1;
            buf = bufpic.getGraphics();
            buf2 = blbuf.getGraphics();
            if(y > 350)
            {
                buf.drawImage(deselect, 114 + xbp, 356, this);
                buf.drawImage(deselect, 232 + xbp, 356, this);
                buf.drawImage(deselect, 349 + xbp, 356, this);
            }
            if(x > 100 + xbp && x < 160 + xbp && y > 350)
            {
                sel = 1;
                buf.drawImage(select, 114 + xbp, 356, this);
            }
            if(x > 210 + xbp && x < 280 + xbp && y > 350)
            {
                sel = 2;
                buf.drawImage(select, 232 + xbp, 356, this);
            }
            if(x > 320 + xbp && x < 390 + xbp && y > 350)
            {
                sel = 3;
                buf.drawImage(select, 349 + xbp, 356, this);
            }
            addblock(mx, my);
        }
        return true;
    }

    public void newgame()
    {
        levno = getParameter("levno").charAt(0) - 65;
        percent = 0;
        showpc = 1;
        grablevs();
        loadaudio();
        loadpics();
        showpc = 0;
        loadlevel();
        drawlevel();
        initfleas();
        tscr = 0;
        ended = 0;
        quit = new Button("Quit");
        quit.setFont(f);
        quit.reshape(540, 355, 40, 25);
        add(quit);
        restart = new Button("Restart");
        restart.setFont(f);
        restart.reshape(590, 355, 40, 25);
        add(restart);
        pause = new Button("Pause");
        pause.setFont(f);
        pause.reshape(480, 355, 50, 25);
        add(pause);
        repaint();
        ended = 0;
    }

    public void fixblock(int xb, int yb)
    {
        fixx[nofix] = xb;
        fixy[nofix] = yb;
        nofix++;
        calcfans();
    }

    public void init()
    {
        timer = new TimerThread();
        timer.start();
        setLayout(null);
        bl[0] = getImage(getCodeBase(), "block0.gif");
        bl[1] = getImage(getCodeBase(), "block1.gif");
        bl[2] = getImage(getCodeBase(), "block2.gif");
        bl[3] = getImage(getCodeBase(), "block3.gif");
        bl[4] = getImage(getCodeBase(), "block4.gif");
        bl[5] = getImage(getCodeBase(), "block5.gif");
        bl[6] = getImage(getCodeBase(), "block6.gif");
        bl[7] = getImage(getCodeBase(), "block7.gif");
        bl[8] = getImage(getCodeBase(), "block8.gif");
        bl[9] = getImage(getCodeBase(), "block9.gif");
        bl[10] = getImage(getCodeBase(), "block10.gif");
        bl[11] = getImage(getCodeBase(), "block11.gif");
        bl[12] = getImage(getCodeBase(), "block12.gif");
        bl[13] = getImage(getCodeBase(), "block13.gif");
        bl[14] = getImage(getCodeBase(), "block14.gif");
        bl[15] = getImage(getCodeBase(), "block15.gif");
        bl[16] = getImage(getCodeBase(), "block16.gif");
        bl[17] = getImage(getCodeBase(), "block17.gif");
        bl[18] = getImage(getCodeBase(), "block18.gif");
        bl[19] = getImage(getCodeBase(), "block19.gif");
        bl[20] = getImage(getCodeBase(), "block20.gif");
        bl[21] = getImage(getCodeBase(), "block21.gif");
        bl[22] = getImage(getCodeBase(), "block22.gif");
        bl[23] = getImage(getCodeBase(), "block23.gif");
        bl[24] = getImage(getCodeBase(), "block24.gif");
        bl[25] = getImage(getCodeBase(), "block25.gif");
        bl[26] = getImage(getCodeBase(), "block26.gif");
        bl[27] = getImage(getCodeBase(), "block27.gif");
        bl[28] = getImage(getCodeBase(), "block28.gif");
        bl[29] = getImage(getCodeBase(), "block29.gif");
        bl[30] = getImage(getCodeBase(), "block30.gif");
        flea[0] = getImage(getCodeBase(), "flea1.gif");
        flea[1] = getImage(getCodeBase(), "flea2.gif");
        flea[2] = getImage(getCodeBase(), "flea3.gif");
        flea[3] = getImage(getCodeBase(), "flea4.gif");
        select = getImage(getCodeBase(), "select.gif");
        deselect = getImage(getCodeBase(), "deselect.gif");
        title = getImage(getCodeBase(), "title.gif");
        win = getImage(getCodeBase(), "win.gif");
        f = new Font("TimesRoman", 0, 13);
        f2 = new Font("TimesRoman", 0, 13);
        f3 = new Font("TimesRoman", 0, 100);
        f4 = new Font("TimesRoman", 0, 36);
        antigrav = getAudioClip(getCodeBase(), "antigrav.au");
        balloon = getAudioClip(getCodeBase(), "balloon.au");
        bomb = getAudioClip(getCodeBase(), "bomb.au");
        bridge = getAudioClip(getCodeBase(), "bridge.au");
        button = getAudioClip(getCodeBase(), "button.au");
        click = getAudioClip(getCodeBase(), "click.au");
        deadsnd = getAudioClip(getCodeBase(), "dead.au");
        dead2 = getAudioClip(getCodeBase(), "dead2.au");
        exitsnd = getAudioClip(getCodeBase(), "exit.au");
        spring = getAudioClip(getCodeBase(), "spring.au");
        teleport = getAudioClip(getCodeBase(), "teleport.au");
        bufpic = createImage(644, 440);
        blbuf = createImage(20, 20);
        for(int n = 0; n < 50; n++)
        {
            anim1[n] = createImage(20, 20);
            anim2[n] = createImage(20, 20);
        }

    }

    public void loadlevel()
    {
        wno = 0;
        exit = 0;
        nospid = 0;
        tno = 0;
        nofans = 0;
        pstate = 0;
        g1 = 1;
        g4 = 4;
        g16 = 16;
        gy = 0;
        gy2 = 0;
        gco = 0;
        fno = num[levno];
        for(int a = 0; a < 40; a++)
        {
            for(int b = 0; b < 21; b++)
                map[a][b] = levstr[levno * 840 + a * 21 + b];

        }

        for(int a = 0; a < 40; a++)
        {
            for(int b = 0; b < 21; b++)
            {
                if(map[a][b] == '\017')
                    map[a][b + 2] = '\007';
                if(map[a][b] == '\001')
                {
                    ex = a * 16 + 8;
                    ey = b * 16 + 16;
                }
                if(map[a][b] == '\b')
                {
                    makeanim(a, b, 9, 8);
                    wm[wno - 1] = 1;
                }
                if(map[a][b] == '\r')
                    makeanim(a, b, 13, 27);
                if(map[a][b] == '\016')
                {
                    makeanim(a, b, 14, 28);
                    fanx[nofans] = a;
                    fany[nofans] = b;
                    fand[nofans] = 0;
                    nofans++;
                }
                if(map[a][b] == '\020')
                {
                    if(tno == 0)
                    {
                        tx1 = a;
                        ty1 = b;
                        tno = 1;
                    } else
                    {
                        tx2 = a;
                        ty2 = b;
                        tno = 2;
                    }
                    makeanim(a, b, 16, 29);
                }
            }

        }

        calcfans();
    }

    public boolean mouseDrag(Event e, int x, int y)
    {
        mx = x;
        my = y;
        return true;
    }

    public void drawlevel()
    {
        buf = bufpic.getGraphics();
        buf.setColor(Color.black);
        buf.fillRect(0, 0, 644, 400);
        for(int a = 39; a >= 0; a--)
        {
            for(int b = 20; b >= 0; b--)
            {
                char c = map[a][b];
                if(c != 0)
                    buf.drawImage(bl[c], a * 16, b * 16, this);
                for(int x = 0; x < 4; x++)
                {
                    for(int y = 0; y < 4; y++)
                        if(c == 0)
                            lvl[a * 4 + x][b * 4 + y] = 0;
                        else
                        if(c == '\001')
                            lvl[a * 4 + x][b * 4 + y] = 1;
                        else
                        if(c == '\003')
                        {
                            if(x == 3 - y)
                                lvl[a * 4 + x][b * 4 + y] = 1;
                            else
                                lvl[a * 4 + x][b * 4 + y] = 0;
                        } else
                        if(c == '\004')
                        {
                            if(x == y)
                                lvl[a * 4 + x][b * 4 + y] = 1;
                            else
                                lvl[a * 4 + x][b * 4 + y] = 0;
                        } else
                        if(c == '\b')
                            lvl[a * 4 + x][b * 4 + y] = -1;
                        else
                        if(c == '\022')
                            lvl[a * 4 + x][b * 4 + y] = -1;
                        else
                        if(c == '\013')
                        {
                            if(y == 3)
                                lvl[a * 4 + x][b * 4 + y] = 1;
                            else
                                lvl[a * 4 + x][b * 4 + y] = 0;
                        } else
                        {
                            lvl[a * 4 + x][b * 4 + y] = 2;
                        }

                }

            }

        }

        buf.drawImage(bl[2], 117 + xbp, 360, this);
        buf.drawImage(bl[3], 235 + xbp, 360, this);
        buf.drawImage(bl[4], 352 + xbp, 360, this);
        buf.drawImage(select, 114 + xbp, 356, this);
        sel = 1;
        buf.setFont(f2);
        buf.setColor(Color.white);
        buf.drawString("Password: " + pass[levno], 10, 357);
        buf.drawString("Fleas: " + String.valueOf(num[levno]), 10, 371);
        buf.drawString("Rescue: " + String.valueOf(sav[levno]), 10, 385);
    }

    public void loadpics()
    {
        imageTracker = new MediaTracker(this);
        tscr = 4;
        mes = new Button("Please Wait Loading Graphics ...");
        mes.setFont(f);
        mes.reshape(202, 279, 239, 30);
        add(mes);
        for(int n = 0; n < 15; n++)
            imageTracker.addImage(bl[n], 0);

        for(int n = 15; n < 31; n++)
            imageTracker.addImage(bl[n], 1);

        for(int n = 0; n < 4; n++)
            imageTracker.addImage(flea[n], 2);

        imageTracker.addImage(select, 2);
        imageTracker.addImage(deselect, 2);
        try
        {
            imageTracker.waitForID(0);
        }
        catch(InterruptedException e) { }
        dopercent(80);
        try
        {
            imageTracker.waitForID(1);
        }
        catch(InterruptedException e) { }
        dopercent(90);
        try
        {
            imageTracker.waitForID(2);
        }
        catch(InterruptedException e) { }
        dopercent(100);
        remove(mes);
        tscr = 3;
    }

    public void contgame()
    {
        prompt1 = new TextField("Enter Level Password");
        prompt1.setFont(f);
        prompt1.reshape(202, 279, 140, 30);
        prompt1.setEditable(false);
        add(prompt1);
        prompt2 = new TextField(20);
        prompt2.setFont(f);
        prompt2.reshape(350, 279, 91, 30);
        add(prompt2);
        prompt2.requestFocus();
    }

    TimerThread timer;
    int dpy;
    int showpc;
    int percent;
    AudioClip antigrav;
    AudioClip balloon;
    AudioClip bomb;
    AudioClip bridge;
    AudioClip button;
    AudioClip click;
    AudioClip deadsnd;
    AudioClip dead2;
    AudioClip exitsnd;
    AudioClip spring;
    AudioClip teleport;
    URL fileURL;
    InputStream input;
    DataInputStream dataInput;
    Image bl[];
    Image bufpic;
    Image flea[];
    Image select;
    Image deselect;
    Image blbuf;
    Image title;
    Image win;
    int tscr;
    int pstate;
    Button but1;
    Button but2;
    Button mes;
    Button quit;
    Button restart;
    Button pause;
    TextField prompt1;
    TextField prompt2;
    String pword;
    MediaTracker imageTracker;
    Graphics buf;
    Graphics buf2;
    Font f;
    Font f2;
    Font f3;
    Font f4;
    int hei;
    int fx[];
    int fy[];
    int fxd[];
    int dead[];
    int fs[];
    int fr[];
    int mx;
    int my;
    int mbut;
    int ended;
    int ex;
    int ey;
    int no;
    int tic;
    int fno;
    int exit;
    int sel;
    int xbp;
    char map[][];
    int lvl[][];
    int wno;
    int wx[];
    int wy[];
    int wm[];
    int wa[];
    int wb[];
    int animco;
    int nospid;
    int spidx[];
    int spidy[];
    int spidp[];
    int spidd[];
    int nofans;
    int fanx[];
    int fany[];
    int fand[];
    int fanx1[];
    int fany1[];
    int fanx2[];
    int fany2[];
    int tx1;
    int ty1;
    int tx2;
    int ty2;
    int tno;
    int g1;
    int g4;
    int g16;
    int gy;
    int gy2;
    int levno;
    int gco;
    int nofix;
    int fixx[];
    int fixy[];
    Image anim1[];
    Image anim2[];
    int numlev;
    int num[];
    int sav[];
    String pass[];
    char levstr[];

    static class TimerThread extends Thread
    {

        TimerThread()
        {
            timer = 0;
            delay = 40;
        }

        public void run()
        {
            while(true) 
            {
                try
                {
                    Thread.sleep(delay);
                }
                catch(InterruptedException e) { }
                timer = 0;
            }
        }

        public int timer;
        public int delay;
    }
}