'use strict';

import gulp from 'gulp';
import sass from 'gulp-sass';
import autoprefixer from 'gulp-autoprefixer';
import sourcemaps from 'gulp-sourcemaps';
import cleancss from 'gulp-clean-css';
import concat from 'gulp-concat';
import rename from 'gulp-rename';
import addsrc from 'gulp-add-src';
import uglify from 'gulp-uglify';
import watch from 'gulp-watch';
import babelify from 'babelify';
import browserify from 'browserify';
import source from 'vinyl-source-stream';
import babel from 'gulp-babel';

const dirs = {
    src: 'src',
    dest: 'build/assets/webroot'
};

const htmlDir = `${dirs.src}/pages`;
const imgDir = `${dirs.src}/img`;

const styleConfig = {
    src: [
        `${dirs.src}/styles/main.scss`
    ],
    dest: `${dirs.dest}/`,
    options: {
        outputStyle: 'compressed'
    }
};

const jsConfig = {
    src: [
        `${dirs.src}/js/app.js`
    ],
    dest: `${dirs.dest}/`,
    other: [`${dirs.src}/lib/jquery/dist/jquery.min.js`]
};

gulp.task('styles', () => {
    console.log(styleConfig.src);
    return gulp.src(styleConfig.src)
        .pipe(sass.sync((styleConfig.options)))
        .pipe(autoprefixer())
        .pipe(cleancss({rebase: false}))
        .pipe(concat('app.css'))
        .pipe(gulp.dest(styleConfig.dest));
});

gulp.task('js', () => {
    gulp.src(jsConfig.src)
        .pipe(babel())
        .pipe(addsrc(jsConfig.other))
        .pipe(concat('app.js'))
        .pipe(uglify())
        .pipe(gulp.dest(jsConfig.dest));
});

gulp.task('html', () => {
    gulp.src([`${htmlDir}/**`])
        .pipe(gulp.dest(`${dirs.dest}/`));
});

gulp.task('img', () => {
    gulp.src([`${imgDir}/**`])
        .pipe(gulp.dest(`${dirs.dest}/`));
});

gulp.task('build', ['html', 'img', 'styles', 'js']);
