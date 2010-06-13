
#
# Cleans all the side effects of latex generation.
#
clean_temp() {
    # Clean all local latex barf
    if [ -f *.log ];
    then
        echo "Clearing *.log"
        rm *.log
    fi
    if [ -f *.aux ];
    then
        echo "Clearing *.aux"
        rm *.aux
    fi
    if [ -f *.out ];
    then
        echo "Clearing *.out"
        rm *.out
    fi
    if [ -f *.ps ];
    then
        echo "Clearing *.ps"
        rm *.ps
    fi
    if [ -f *.dvi ];
    then
        echo "Clearing *.dvi"
        rm *.dvi
    fi
    if [ -f *.toc ];
    then
        echo "Clearing *.toc"
        rm *.toc
    fi
    if [ -f html/labels.pl ];
    then
        echo "Removing html/labels.pl"
        rm html/labels.pl
    fi
    if [ -f html/WARNINGS ];
    then
        echo "Removing html/WARNINGS"
        rm html/WARNINGS
    fi
    if [ -f .log ];
    then
        echo "Removing .log"
        rm .log
    fi
    # Clear the html directory

}

#
# Clean the output files as well
#
clean_output() {

    if [ -f simplerowlog.pdf ];
    then
        echo "Removing simplerowlog.pdf"
        rm *.pdf
    fi
    if [ -f html/simplerowlog.css ];
    then
        echo "Removing html/simplerowlog.css"
        rm html/simplerowlog.css
    fi
    if [ -f html/index.html ];
    then
        echo "Removing html/*.html"
        find . -type f -name "*.html" -exec rm -f {} \;  
    fi
}


clean() {
    echo "Cleaning output files ..."
    clean_output
    echo "Cleaning temporary files ..."
    clean_temp
}

#
# TODO:
texinfo() {
    latex2html simplerowlog.tex -dir texi -split 0
    html2texi texi/index.html
    mv texi.texi simplerowlog.texi
}

compile() {
    clean_output
    echo "Compiling pdf..."
    # We run latex first to get a TOC etc.
    pdflatex -interaction=nonstopmode simplerowlog.tex &> /dev/null
    pdflatex -interaction=nonstopmode simplerowlog.tex
    echo "Compiling html documentation..."
    latex2html simplerowlog.tex -split 4 -long_titles 10 -show_section_numbers -dir html 
    echo "Done html, cleaning up."
    clean_temp
    echo "Completed!"
}

case "$1" in
    make)
        compile
        ;;
    clean)
        clean
        ;;
    easter)
        echo "egg"
        ;;
esac
