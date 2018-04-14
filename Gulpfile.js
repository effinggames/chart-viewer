var gulp = require('gulp');
var rev = require('gulp-rev');
var gulpif = require('gulp-if');
var clean = require('gulp-clean');
var gutil = require('gulp-util');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var stylus = require('gulp-stylus');
var stylusNodes = require('stylus').nodes;
var webpack = require('webpack-stream');
var imageMin = require('gulp-imagemin');
var cssMin = require('gulp-minify-css');
var sourcemaps = require('gulp-sourcemaps');
var nib = require('nib');
var path = require('path');
var wait = require('gulp-wait');
var es = require('event-stream');
var through = require('through2');
var merge = require('event-stream').concat;
var browserSync = require('browser-sync');
var reloadMe = require('browser-sync').reload;

var publicDir = __dirname + '/public',
  publicImgDir = __dirname + '/public/img';

var fileHashes = {};
var reloadTimeout;

var webpackAppJS = function(minifyMe) {
  return gulp
    .src('./assets/scripts/App.js')
    .pipe(
      webpack({
        devtool: 'inline-source-maps',
        module: {
          loaders: [
            {
              test: /.js$/,
              loader: 'ng-annotate'
            },
            {
              test: /.js$/,
              exclude: /(node_modules|bower_components)/,
              loader: 'babel-loader',
              query: {
                cacheDirectory: true,
                presets: ['es2015']
              }
            }
          ]
        }
      })
    )
    .pipe(concat('app.js'))
    .pipe(gulpif(minifyMe, uglify()))
    .pipe(gulp.dest(publicDir));
};

var concatCSS = function(minifyMe) {
  return gulp
    .src([
      './assets/styles/reset.styl',
      './assets/styles/framework.styl',
      './assets/styles/main.styl'
    ])
    .pipe(
      stylus({
        use: [nib()],
        define: {
          getVersionedAsset: function(urlNode) {
            var versionHash = fileHashes[urlNode.val] || '123';
            var urlStr = versionHash
              ? urlNode.val + '?v=' + versionHash
              : urlNode.val;
            return new stylusNodes.Ident(urlStr);
          }
        }
      })
    )
    .pipe(concat('app.css'))
    .pipe(gulpif(minifyMe, cssMin()))
    .pipe(gulp.dest(publicDir));
};

var copyAssets = function() {
  return gulp
    .src(['./assets/img/**/*'], {base: './assets'})
    .pipe(filterEmptyDirs())
    .pipe(gulp.dest(publicDir));
};

//removes empty dirs from stream
var filterEmptyDirs = function() {
  return es.map(function(file, cb) {
    if (file.stat.isFile()) {
      return cb(null, file);
    } else {
      return cb();
    }
  });
};

var minifyImages = function() {
  return gulp
    .src(publicImgDir + '/**/*')
    .pipe(imageMin())
    .pipe(gulp.dest(publicImgDir));
};

//opens up browserSync url
var syncMe = function() {
  browserSync({
    proxy: 'localhost:8000',
    open: false
  });
};

//gets a gulp-rev stream and converts it to a hashes.json
var getHashes = function() {
  // Note that we're not emitting the files here... this consumer effectively
  // stores the rev hashes then swallows the entire pipeline.
  var collect = function(file, enc, cb) {
    if (file.revHash) {
      var filePath = file.revOrigPath.slice(file.revOrigBase.length);
      fileHashes[filePath] = file.revHash;
    }

    return cb();
  };

  // Once the stream is finished, we'll emit a single "hashes.json" file...
  var emit = function(cb) {
    //var file = new gutil.File({
    //    base: __dirname,
    //    cwd:  __dirname,
    //    path: __dirname+'/hashes.json'
    //});
    //
    //file.contents = new Buffer(JSON.stringify(fileHashes, null, '\t'));
    //this.push(file);

    return cb();
  };

  return through.obj(collect, emit);
};

//calculate file hashes for cache busting
var revFiles = function() {
  return gulp
    .src(publicDir + '/**/*')
    .pipe(gulp.dest(publicDir))
    .pipe(rev())
    .pipe(getHashes())
    .pipe(gulp.dest(__dirname));
};

//cleans build folder
gulp.task('clean', function() {
  console.log('Cleaning:', publicDir);
  return gulp.src(publicDir, {read: false}).pipe(clean());
});

//build + watching, for development
gulp.task('default', ['clean'], function() {
  gulp.watch(['./assets/scripts/**/*.js'], function() {
    console.log('File change - webpackAppJS()');
    webpackAppJS().on('end', function() {
      clearTimeout(reloadTimeout);
      reloadTimeout = setTimeout(function() {
        reloadMe();
      }, 1500);
    });
  });
  gulp.watch('./assets/styles/**/*.styl', function() {
    console.log('File change - concatCSS()');
    concatCSS()
      .pipe(wait(1500))
      .pipe(reloadMe({stream: true}));
  });
  gulp.watch(['./assets/img/**/*'], function() {
    console.log('File change - copyAssets()');
    copyAssets()
      .pipe(wait(1500))
      .pipe(reloadMe({stream: true}));
  });
  gulp.watch(['./app/views/**/*'], function() {
    console.log('File change - templates');
    clearTimeout(reloadTimeout);
    reloadTimeout = setTimeout(function() {
      reloadMe();
    }, 1500);
  });

  return merge(copyAssets(), concatCSS(), webpackAppJS()).on('end', function() {
    syncMe();
  });
});

//production build task
gulp.task('build', ['clean'], function() {
  return merge(copyAssets(), concatCSS(false), webpackAppJS(true)).on(
    'end',
    function() {
      minifyImages();
      revFiles().on('end', function() {
        concatCSS(true);
      });
    }
  );
});
